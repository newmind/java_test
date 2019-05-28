package kr.osci.webservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kr.osci.webservice.model.Contact;
import kr.osci.webservice.repository.ContactRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping({ "/contacts" })
public class ContactController {

    private ContactRepository repository;

    // 모든 Contacts (GET /contacts)
    @GetMapping
    public List findAll() {
        return repository.findAll();
    }

    // ID 에 맞는 contact (GET /contacts/{id})
    @GetMapping(path = { "/{id}" })
    public ResponseEntity<Contact> findById(@PathVariable long id) {
        return repository.findById(id).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    // Contact 만들기 (POST /contacts)
    @PostMapping
    public Contact create(@RequestBody Contact contact) {
        return repository.save(contact);
    }

    // Contact 업데이트 (PUT /contacts/{id})
    @PutMapping(value = "/{id}")
    public ResponseEntity<Contact> update(@PathVariable("id") long id, @RequestBody Contact contact) {
        return repository.findById(id).map(record -> {
            record.setName(contact.getName());
            record.setPhone(contact.getPhone());
            record.setNote(contact.getNote());
            Contact updated = repository.save(record);
            return ResponseEntity.ok().body(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 해당 ID의 Contact 지우기 (DELETE /contacts/{id})
    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

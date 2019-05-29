package kr.osci.webservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import kr.osci.webservice.model.Contact;
import kr.osci.webservice.service.ContactService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping({ "/contacts" })
public class ContactController {

//    private ContactRepository repository;
    private ContactService service;

    // 모든 Contacts (GET /contacts)
    @GetMapping
    public List<Contact> findAll() {
        return service.findAll();
    }

    // ID 에 맞는 contact (GET /contacts/{id})
    @GetMapping(path = { "/{id}" })
    public ResponseEntity<Contact> findById(@PathVariable long id) {
        return service.findById(id).map(record -> ResponseEntity.ok().body(record))
                .orElse(ResponseEntity.notFound().build());
    }

    // Contact 만들기 (POST /contacts)
    @PostMapping
    public Contact create(@RequestBody Contact contact) {
        return service.save(contact);
    }

    // Contact 업데이트 (PUT /contacts/{id})
    @PutMapping(value = "/{id}")
    public ResponseEntity<Contact> update(@PathVariable("id") long id, @RequestBody Contact contact) {
        return service.findById(id).map(record -> {
            record.setName(contact.getName());
            record.setPhone(contact.getPhone());
            record.setNote(contact.getNote());
            Contact updated = service.save(record);
            return ResponseEntity.ok().body(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    // 해당 ID의 Contact 지우기 (DELETE /contacts/{id})
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        return service.findById(id).map(record -> {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}

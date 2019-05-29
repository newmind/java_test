package kr.osci.webservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import kr.osci.webservice.model.Contact;
import kr.osci.webservice.repository.ContactRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ContactService {
    private final ContactRepository repository;
    
    public List<Contact> findAll() {
        return repository.findAll();
    }
    
    public Optional<Contact> findById(Long id) {
        return repository.findById(id);
    }
    
    public Contact save(Contact contact) {
        return repository.save(contact);
    }
    
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}

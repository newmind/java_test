package kr.osci.webservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.osci.webservice.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>{

}

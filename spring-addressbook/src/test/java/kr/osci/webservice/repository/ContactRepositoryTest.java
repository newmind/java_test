package kr.osci.webservice.repository;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import kr.osci.webservice.model.Contact;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContactRepositoryTest {

    @Autowired
    ContactRepository repository;
    
    @After
    public void cleanup() {
        /** 
        이후 테스트 코드에 영향을 끼치지 않기 위해 
        테스트 메소드가 끝날때 마다 repository 전체 비우는 코드
        **/
//        repository.deleteAll();
    }
    
    @Test
    public void test_저장_불러오기() {
        repository.save(new Contact(null, "jgkim", "01055557777", "서울 송파구 잠실", ""));
        repository.save(new Contact(null, "kim", "123456789", "서울 강남구 서초", ""));
        
        List<Contact> list = repository.findAll();

        Contact address = list.get(0);
        assertThat(address.getName(), is("jgkim"));
        assertThat(address.getPhone(), is("01055557777"));
    }
    

}

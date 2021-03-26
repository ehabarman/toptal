package com.toptal.backend.repository;

import com.toptal.backend.model.Record;
import com.toptal.backend.model.User;
import com.toptal.backend.util.helpers.DateTimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RecordRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Test
    public void addRecordsToUsers_deleteRecordFromUsers() {
        User user1 = new User("test1", "test1");
        User user2 = new User("test2", "test2");
        userRepository.save(user1);
        userRepository.save(user2);
        LocalDate localDateUser11 = DateTimeUtil.buildDate("1996-6-12");
        LocalDate localDateUser12 = DateTimeUtil.buildDate("1996-6-13");
        LocalDate localDateUser21 = DateTimeUtil.buildDate("1996-6-14");
        LocalDate localDateUser22 = DateTimeUtil.buildDate("1996-6-15");
        Record[] records = {
                new Record(localDateUser11, user1),
                new Record(localDateUser12, user1),
                new Record(localDateUser21, user2),
                new Record(localDateUser22, user2),
        };

        for (int i = 0; i < records.length; i++) {
            records[i] = recordRepository.save(records[i]);
        }

        Assert.assertEquals(4, recordRepository.findAll().size());
        Assert.assertEquals(user1.getUsername(), recordRepository.findRecordByUserAndId(user1, 1L).getUser().getUsername());
        Assert.assertNull(recordRepository.findRecordByUserAndId(user1, 3L));
        Assert.assertEquals(user2.getUsername(), recordRepository.findRecordByUserAndId(user2, 3L).getUser().getUsername());
        Assert.assertNull(recordRepository.findRecordByUserAndId(user2, 1L));
        Assert.assertEquals(records[0].getDate(), recordRepository.findRecordByUserAndDate(user1, localDateUser11).getDate());
        Assert.assertNull(recordRepository.findRecordByUserAndDate(user1, localDateUser21));
        Assert.assertEquals(records[2].getDate(), recordRepository.findRecordByUserAndDate(user2, localDateUser21).getDate());

        for (Record record : records) {
            recordRepository.deleteById(record.getId());
        }
        Assert.assertEquals(0, recordRepository.findAll().size());
    }

}

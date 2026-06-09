package edu.hbucm.ending;

import edu.hbucm.ending.dao.entity.Student;
import edu.hbucm.ending.dao.mapper.StudentMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest

class EndingApplicationTests {

	@Autowired
	StudentMapper mapper;

	@Test
	void test_getAll() {
		List<Student> students=mapper.getAllStudents();
		for(Student student:students)
			System.err.println(student);
	}

}


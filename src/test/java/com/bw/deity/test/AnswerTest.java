package com.bw.deity.test;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.magicBook.cms.dao.DeityMapper;
import com.magicBook.cms.domain.DeityExample;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-beans.xml"})
public class AnswerTest {

	@Resource
	private DeityMapper mapper;
	
	@Test
	public void Test1()
	{
		String fname = "大数据_专高3_《分布式架构》第4单元：关于Dubbo，以下用于提供方配置的标签是".replaceAll("↵", "").replaceAll(" ", "").replaceAll("\\s*|\t|\r|\n", "");
		List<Map> selectLikeProblem = mapper.selectLikeProblem(fname, "1");
		System.out.println(selectLikeProblem.size());
		System.out.println(selectLikeProblem.get(0).toString());
	}
}

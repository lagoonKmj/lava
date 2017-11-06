package com.lagoon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.lagoon.job.SQA엘라스틱서치;


/**
 * @author lagoon
 *
 */
@Controller
public class LavaController {

    @Autowired private SQA엘라스틱서치 job;
    
    @RequestMapping(value = "/get__1.do", method = {RequestMethod.GET, RequestMethod.POST})
    public void get__1() {
        System.out.println("#Lagoon's Java Test Project");
        job.조회();
    }

    
}
package com.dmm.task.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

	
@Controller
public class EditController {
	@RequestMapping("/edit")
	String edit() {
		return "edit";
	}
}




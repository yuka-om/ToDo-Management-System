package com.dmm.task.form;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TaskForm {
	private String title;
	private String text;
	private LocalDate date;
}

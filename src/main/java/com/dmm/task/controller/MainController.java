package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.dmm.task.data.entity.Tasks;
import com.dmm.task.data.repository.TaskRepository;
import com.dmm.task.form.TaskForm;
import com.dmm.task.service.AccountUserDetails;

@Controller
public class MainController {

	@GetMapping("/main")
	public String main(Model model) {

		//1. 2次元表になるので、ListのListを用意する	 
		List<List<LocalDate>> month = new ArrayList<>();
		//2. 1週間分のLocalDateを格納するListを用意する		
		List<LocalDate> week = new ArrayList<>();

		LocalDate day;
		day = LocalDate.now();  // 現在日時を取得

		//3. その月の1日のLocalDateを取得する
		day = LocalDate.of(day.getYear(), day.getMonthValue(), 1); // 現在日時からその月の1日を取得

		//4.曜日を表すDayOfWeekを取得
		DayOfWeek w = day.getDayOfWeek();

		//上で取得したLocalDateに曜日の値（DayOfWeek#getValue)をマイナスして前月分のLocalDateを求める
		day = day.minusDays(w.getValue());
		//5. 1日ずつ増やしてLocalDateを求めていき、
		for(int i = 0; i < 7; i++){
			//2．で作成したListへ格納していき、 
			week.add(day);
			day = day.plusDays(1);
		}
		//1週間分詰めたら1．のリストへ格納する
		month.add(week);
		week = new ArrayList<>();
		//6. 2週目以降は単純に1日ずつ日を増やしながらLocalDateを求めてListへ格納していき、
		//土曜日になったら1．のリストへ格納して新しいListを生成する（月末を求めるにはLocalDate#lengthOfMonth()を使う）
		if(day.lengthOfMonth() % 7 == 0){
			for (int j = 0; j < day.lengthOfMonth()/7-1; j++ ) {
				for(int i = 0; i < 7; i++){
					week.add(day);
					day = day.plusDays(1);
				}
				month.add(week);
				week = new ArrayList<>();
			}
		}else{for (int j = 0; j < day.lengthOfMonth()/7; j++ ) {
			for(int i = 0; i < 7; i++){
				week.add(day);
				day = day.plusDays(1);
			}
			month.add(week);
			week = new ArrayList<>();
		}
		}
		
		// ★日付とタスクを紐付けるコレクション
		MultiValueMap<LocalDate, Tasks> tasks = new LinkedMultiValueMap<LocalDate, Tasks>();
		model.addAttribute("matrix", month);
		model.addAttribute("tasks", tasks);
		return "main";
	}
	
	@Autowired
	private TaskRepository repo;

	// タスク登録画面の表示用
	@GetMapping("/main/create/{date}")
	public String create(Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		return "create";
	}

	// タスク登録用
	@PostMapping("main/create")
	public String createTasks(TaskForm taskForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user, Model model) {
		Tasks tasks = new Tasks();
		tasks.setName(user.getName());
		tasks.setTitle(taskForm.getTitle());
		tasks.setText(taskForm.getText());
		tasks.setDate(taskForm.getDate());
		
		repo.save(tasks);
		return "redirect:/main";
	}

	// タスク編集用
	@GetMapping("main/edit/{id}")
	public String edit(Model model) {
		return "edit";
	}
}


package com.dmm.task.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class MainController{

	@Autowired
	private TaskRepository repo;

	@GetMapping("/main")
	public String main(Model model,@AuthenticationPrincipal AccountUserDetails user,
			@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		//1. 2次元表になるので、ListのListを用意する	 
		List<List<LocalDate>> month = new ArrayList<>();
		//2. 1週間分のLocalDateを格納するListを用意する		
		List<LocalDate> week = new ArrayList<>();
		LocalDate day, start;

		// ★今月 or 前月 or 翌月を判定
		if (date == null) {  // 今月と判断
			// その月の1日を取得する
			day = LocalDate.now();  // 現在日時を取得
		} else {  // 前月 or 翌月と判断
			day = date;  // 引数で受け取った日付をそのまま使う
		}

		model.addAttribute("prev", day.minusMonths(1));
		model.addAttribute("month", day.format(DateTimeFormatter.ofPattern("yyyy年MM月")));
		model.addAttribute("next", day.plusMonths(1));

		//3. その月の1日のLocalDateを取得する
		day = LocalDate.of(day.getYear(), day.getMonthValue(), 1); // 現在日時からその月の1日を取得
		int lastDay = day.lengthOfMonth(); // 当月の日数(int型)
		//4.曜日を表すDayOfWeekを取得
		DayOfWeek w = day.getDayOfWeek();
		start = day;

		//上で取得したLocalDateに曜日の値（DayOfWeek#getValue)をマイナスして前月分のLocalDateを求める
		if (!(w.getValue() == 7)) { // 前月分が必要な場合（初日が日曜日以外）
			day = day.minusDays(w.getValue());
			start = day;
			//5. 1日ずつ増やしてLocalDateを求めていき、
			for(int i = 0; i < 7; i++){
				//2．で作成したListへ格納していき、 
				week.add(day);
				day = day.plusDays(1);
			}
			//1週間分詰めたら1．のリストへ格納する
			month.add(week);
			week = new ArrayList<>();
		}

		int left = lastDay-(7 - w.getValue()); 
		int lines = left / 7;
		if (!(left %7 ==0)) { // leftが7の倍数でなかったら
			lines++;
		}
		for(int j = 0; j < lines ; j++) {  
			for(int i = 0; i < 7; i++){
				week.add(day);
				day = day.plusDays(1);
			}
			month.add(week);
			week = new ArrayList<>();
		}

		List<Tasks> list;
		// ★日付とタスクを紐付けるコレクション
		MultiValueMap<LocalDate, Tasks> tasks = new LinkedMultiValueMap<LocalDate, Tasks>();

		if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
			list = repo.findAllByDateBetween(start, day);
		} else {
			list = repo.findByDateBetween(start, day, user.getName());
		}
		for(Tasks task : list) {
			tasks.add(task.getDate(), task);
		}

		model.addAttribute("matrix", month);
		model.addAttribute("tasks", tasks);

		return "main";
	}

	// タスク登録画面の表示用
	@GetMapping("/main/create/{date}")
	public String create(Model model, @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
		return "create";
	}

	// タスク登録用
	@PostMapping("main/create")
	public String createTasks(TaskForm taskForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user, Model model) {
		Tasks task = new Tasks();
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setText(taskForm.getText());
		task.setDate(taskForm.getDate());
		repo.save(task);
		model.addAttribute("tasks.get(day)", task);

		return "redirect:/main";
	}

	// タスク編集画面の表示用
	@GetMapping("main/edit/{id}")
	public String edit(Model model, @PathVariable Integer id) {
		Tasks task = repo.getById(id);
		model.addAttribute("task", task);
		return "edit";
	}

	// タスク編集登録用
	@PostMapping("/main/edit/{id}")
	public String editTasks(@PathVariable Integer id,TaskForm taskForm, BindingResult bindingResult,
			@AuthenticationPrincipal AccountUserDetails user) {
		Tasks task = new Tasks();
		task.setId(id);
		task.setName(user.getName());
		task.setTitle(taskForm.getTitle());
		task.setText(taskForm.getText());
		task.setDate(taskForm.getDate());
		task.setDone(taskForm.isDone());
		repo.save(task);
		return "redirect:/main";
	}

	// ★タスク削除用
	@PostMapping("/main/delete/{id}")
	public String deletePost(Model model, @PathVariable Integer id) {
		Tasks task = repo.getById(id);
		repo.delete(task);
		return "redirect:/main";
	}
}




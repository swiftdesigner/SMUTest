package com.smu.action;
import java.io.PrintWriter;
import java.util.*;
import com.smu.model.*;
import com.smu.model.Class;
import com.smu.service.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.smu.util.Require;
import com.smu.util.RequireAndScore;
import com.smu.util.StudentAllCaseScore;
import com.smu.util.StudentScore;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.log4j.Logger;
public class ScoreAction extends ActionSupport {
	private IScoreService scoreService;
	private String TId;
	private IUserService userService;
	private IStudentService studentService;
	private IStationService stationService ;
	private ICaseService caseService;
	private IRequirementService requirementService;
	private ITestService iTestService;
	private IClassService classService;
	private Score score;
	private int stc_id;
	private int c_id;
	private String class_name;
	private int testId;
	private String[] newError;
	private int[] scId;
    private String RId;
    private IRequirementStoreService requirementStoreService;
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.LogManager.getLogger(ScoreAction.class);

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public IClassService getClassService() {
		return classService;
	}

	public void setClassService(IClassService classService) {
		this.classService = classService;
	}

	public IScoreService getScoreService() {
		return scoreService;
	}

	public void setScoreService(IScoreService scoreService) {
		this.scoreService = scoreService;
	}

	public String getTId() {
		return TId;
	}

	public void setTId(String TId) {
		this.TId = TId;
	}

	public IStudentService getStudentService() {
		return studentService;
	}

	public void setStudentService(IStudentService studentService) {
		this.studentService = studentService;
	}

	public IStationService getStationService() {
		return stationService;
	}

	public void setStationService(IStationService stationService) {
		this.stationService = stationService;
	}

	public ICaseService getCaseService() {
		return caseService;
	}

	public void setCaseService(ICaseService caseService) {
		this.caseService = caseService;
	}

	public IRequirementService getRequirementService() {
		return requirementService;
	}

	public void setRequirementService(IRequirementService requirementService) {
		this.requirementService = requirementService;
	}

	public ITestService getiTestService() {
		return iTestService;
	}

	public void setiTestService(ITestService iTestService) {
		this.iTestService = iTestService;
	}

	public Score getScore() {
		return score;
	}

	public void setScore(Score score) {
		this.score = score;
	}

	public int getStc_id() {
		return stc_id;
	}

	public void setStc_id(int stc_id) {
		this.stc_id = stc_id;
	}

	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public String getClass_name() {
		return class_name;
	}

	public void setClass_name(String class_name) {
		this.class_name = class_name;
	}


	public int getTestId() {
		return testId;
	}

	public void setTestId(int testId) {
		this.testId = testId;
	}

	public String[] getNewError() {
		return newError;
	}

	public void setNewError(String[] newError) {
		this.newError = newError;
	}

	public String getRId() {
		return RId;
	}

	public void setRId(String RId) {
		this.RId = RId;
	}

	public IRequirementStoreService getRequirementStoreService() {
		return requirementStoreService;
	}

	public void setRequirementStoreService(IRequirementStoreService requirementStoreService) {
		this.requirementStoreService = requirementStoreService;
	}

	public int[] getScId() {
		return scId;
	}

	public void setScId(int[] scId) {
		this.scId = scId;
	}

	public static Logger getLOGGER() {
		return LOGGER;
	}
	/**确认一条成绩*/
	public String commitScore() throws  Exception{
		for (int i = 0;i <= scId.length-1;i++) {
			scoreService.commitScore(scId[i]);
		}
		UnCommitedTests();
		return SUCCESS;
	}
    /** 修改一条还未最终提交的成绩*/
    public String updateScore() throws  Exception{
    	scoreService.updateScore(score.getScId(),score.getScScore(),score.getScTotalScore());
		getUncommitedScoreByTestIdAndTId();
    	return SUCCESS;
	}
	/** 通过成绩 id 获得一条还未提交的成绩*/
	public String getOneCommitedScore() throws Exception{
		 Score score= scoreService.getUncommitedScoreByScoreId(stc_id);
		 Student student = studentService.checkStudent(score.getStudent().getSNo());
		 Requirement r = requirementService.getAllRequirements(score.getCId());
		 String[] names = r.getRName().split("/");
		 String[] contents = r.getRContent().split("/");
		 String[] maxScores = r.getRScore().split("/");
		 String[] scores = score.getScScore().split(",");
		 List<RequireAndScore> r_list = new ArrayList<RequireAndScore>();
		 for(int i = 1;i<= scores.length;i++){
			RequireAndScore require = new RequireAndScore();
			require.setContent(contents[i]);
			require.setScore(maxScores[i]);
			require.setName(names[i]);
			require.setUncommitedScore(scores[i-1]);
			LOGGER.warn(scores[i-1]);
			r_list.add(require);
		 }
		Map requestMap = (Map) ActionContext.getContext().get("request");
		requestMap.put("student",student);
		requestMap.put("RequireAndScore",r_list);
		requestMap.put("RName",names[0]);
		requestMap.put("RContent",contents[0]);
		requestMap.put("RScore",maxScores[0]);
		requestMap.put("scId",stc_id);
		requestMap.put("testId",score.getTId());
         return SUCCESS;
	}
	/** 获得一名老师还未最终提交的考试*/
	public String UnCommitedTests() throws Exception{
		Map session=(Map) ActionContext.getContext().getSession();
		if(!session.get("id").equals(TId)){
			return ERROR;
		}
        List<Score> scores = scoreService.getUncommitedScoresByTId(TId);
		Set<Integer> uncommitedTestIds = new HashSet<Integer>();
        for(int i = 0;i<=scores.size()-1;i++){
        	uncommitedTestIds.add(scores.get(i).getTId());
		}
		List<Test> tests = new ArrayList<Test>();
		for(Iterator<Integer> iterator = uncommitedTestIds.iterator();iterator.hasNext();){
			Test test = new Test();
			test = iTestService.getOneTest(iterator.next());
			tests.add(test);
		}
		Map requestMap = (Map) ActionContext.getContext().get("request");
		requestMap.put("tests",tests);
		return SUCCESS;
	}
	/** 通过考试 ID 和教师 ID 查找还没有最终提交的学生成绩 */
	public String getUncommitedScoreByTestIdAndTId() throws  Exception
	{
        LOGGER.warn(testId);
		List<Score> scores = scoreService.getUncommitedScoreByTestIdAndTId(testId,TId);

		for(int i = 0;i <= scores.size()-1;i++){
			//将 ScScore 属性设置为该学生的名字
			scores.get(i).setScScore(studentService.checkStudent(scores.get(i).getStudent().getSNo()).getSName());
			scores.get(i).setErrors(caseService.getOneCase(scores.get(i).getCId()).getCName());
			LOGGER.warn(scores.get(i).getScScore());
		}
		Map requestMap = (Map) ActionContext.getContext().get("request");
		requestMap.put("scores",scores);
		return SUCCESS;
	}
	/** 提交成绩(未最终提交)*/
	public String addScore() throws Exception
	{
		Map session=(Map) ActionContext.getContext().getSession();
		score.setTName(session.get("userName").toString());
		score.setStatus("no");
		scoreService.addScore(score);
//		Requirement r = requirementService.getAllRequirements(c_id);

		Case cas = new Case();
		cas = caseService.getOneCase(c_id);
		Requirement r = requirementService.getAllRequirements(cas.getCId());
		RequirementStore rStore = requirementStoreService.getOneRequirementStore(r.getRStoreId());
		String[] oldErrors = r.getErrors().split("/");
		String[] oldStoreErrors = rStore.getErrors().split("/");
		//将新产生的错误附加在就错误的后面
		for(int j = 0;j<=newError.length-1;j++){
			if(newError[j].equals("air")){
				continue;
			}
			oldErrors[j] = oldErrors[j] + "," + newError[j].substring(4);
			oldStoreErrors[j] = oldStoreErrors[j] + "," + newError[j].substring(4);
		}
		//通过 set 方法更新易犯错误
		String nowError ="air";
		String nowStoreError ="air";
		for(int k = 0;k <= oldErrors.length-1;k++){
			nowError = nowError + "/" + oldErrors[k];
			nowStoreError = nowStoreError + "/" + oldStoreErrors[k];
		}
		LOGGER.warn(nowError);
		LOGGER.warn(nowError.substring(4));
		r.setErrors(nowError.substring(4));
		rStore.setErrors(nowStoreError.substring(4));
		requirementService.updateErrors(r.getRId(),nowError.substring(4));
		requirementStoreService.updateStoreErrors(rStore.getRId(),nowError.substring(4));
//		int stId = cas.getStation().getStId();
//		Test ttt = new Test();
//		ttt = iTestService.getOneTest(score.getTId());
//		String gradeClassName = ttt.getClassName();
//		String[] className = gradeClassName.split(",");
//		List<Student> students = new ArrayList<Student>();
//		for(int m = 0;m<=className.length-1;m++) {
//			students.addAll(studentService.getStudentsByClass(className[m]));
//		}
//		Map map = new HashMap<>();
//		for(int i = 0;i<students.size();i++){
//			map.put(students.get(i).getSNo(), students.get(i).getSName()+students.get(i).getSNo());
//		}
//		String rcontent = r.getRContent();
//		String rscore = r.getRScore();
//		String name = r.getRName();
//		String[] contents = rcontent.split("/");
//		String[] scores = rscore.split("/");
//		String[] names = name.split("/");
//		String[] errors = r.getErrors().split("/");
//		List<Require> r_list = new ArrayList<Require>();
//		for(int i = 1;i<= scores.length-1;i++){
//			Require require = new Require();
//			require.setContent(contents[i]);
//			require.setScore(scores[i]);
//			require.setName(names[i]);
//			String[] error = errors[i-1].split(",");
//			Map<String,String> errorsMap= new HashMap<String,String>();
//			for(int j = 0;j<= error.length-2;j++){
//				errorsMap.put(error[j+1],error[j+1]);
//			}
//			require.setMap(errorsMap);
//			r_list.add(require);
//		}
		Map requestMap = (Map) ActionContext.getContext().get("request");
//		requestMap.put("stId",stId);
		requestMap.put("CId", c_id);
//		requestMap.put("require", r_list);
//		requestMap.put("RName",names[0]);
//		requestMap.put("RContent",contents[0]);
//		requestMap.put("RScore",scores[0]);
//		requestMap.put("case", cas);
//		requestMap.put("students",map);
		requestMap.put("TId", score.getTId());
//		requestMap.put("RId",RId);
		return SUCCESS;
	}
	public String browseScores() throws Exception{
		Double score;
		int[] score_nums = {30,40,20,50,80,70,40,30,10,5};
		if(class_name.equals("全部全部")){
			String classesString = iTestService.getOneTest(testId).getClassName();
			String[] classes = classesString.split(",");
			List<StudentScore> studentScores = new ArrayList<StudentScore>();
			for(int t = 0;t<=classes.length-1;t++) {
				List<Student> students = studentService.getStudentsByClass(classes[t]);
				List<Station> stations = stationService.gainAllStations(testId);
				int[] st_id = new int[stations.size()];
				for (int j = 0; j <= stations.size() - 1; j++) {
					st_id[j] = stations.get(j).getStId();
				}
				int nums = stations.size();
				for (int m = 0; m <= students.size() - 1; m++) {
					score = 0.00;
					for (int n = 0; n <= st_id.length - 1; n++) {
						List<Score> scores = scoreService.gainScore(students.get(m).getSNo(), st_id[n]);
						if (scores.size() == 1) {
							score = score + scores.get(0).getScTotalScore();
						} else if (scores.size() > 1) {
							Double average_score = 0.00;
							for (int l = 0; l <= scores.size() - 1; l++) {
								average_score = average_score + scores.get(l).getScTotalScore();
							}
							average_score = average_score / scores.size();
							score = score + average_score;
							System.out.println(score);
						} else {
							score = score + 0.00;
						}
					}
					score = score / nums;
					System.out.println(score);
					if (90 <= score && score <= 100)
						score_nums[0]++;
					else if (80 <= score && score <= 90)
						score_nums[1]++;
					else if (70 <= score && score <= 80)
						score_nums[2]++;
					else if (60 <= score && score <= 70)
						score_nums[3]++;
					else if (50 <= score && score <= 60)
						score_nums[4]++;
					else if (40 <= score && score <= 50)
						score_nums[5]++;
					else if (30 <= score && score <= 40)
						score_nums[6]++;
					else if (20 <= score && score <= 30)
						score_nums[7]++;
					else if (10 <= score && score <= 20)
						score_nums[8]++;
					else if (0 <= score && score <= 10)
						score_nums[9]++;
					StudentScore studentScore = new StudentScore();
					studentScore.setS_no(students.get(m).getSNo());
					studentScore.setS_name(students.get(m).getSName());
					studentScore.setS_grade(students.get(m).getSGrade());
					studentScore.setS_class(students.get(m).getMclass().getClassName());
					studentScore.setScore(score);
					studentScores.add(studentScore);
				}
			}
			Map requestMap = (Map) ActionContext.getContext().get("request");
			requestMap.put("score_nums", score_nums);
			requestMap.put("studentScore", studentScores);
			requestMap.put("class_name", class_name);
			requestMap.put("testId",testId);
			return SUCCESS;
		}
		List<Student> students = studentService.getStudentsByClass(class_name);
//		String[] s_no = new String[students.size()];
//		for(int i=0;i<=students.size()-1;i++){
//			s_no[i] = students.get(i).getSNo();
//		}
		List<Station> stations = stationService.gainAllStations(testId);
		int[] st_id = new int[stations.size()];
		for(int j = 0;j<=stations.size()-1;j++){
			st_id[j] = stations.get(j).getStId();
		}
		int nums = stations.size();
		List<StudentScore> studentScores = new ArrayList<StudentScore>();
		for(int m = 0;m<=students.size()-1;m++){
			score = 0.00;
			for(int n = 0;n<=st_id.length-1;n++){
				
				List<Score> scores = scoreService.gainScore(students.get(m).getSNo(),st_id[n]);
				
				if(scores.size()==1){
					score = score + scores.get(0).getScTotalScore();
				}
				else if(scores.size()>1){
					Double average_score = 0.00;
					for(int l = 0;l<=scores.size()-1;l++){
						
						average_score = average_score + scores.get(l).getScTotalScore();	
						
					}
					average_score = average_score/scores.size();
					score = score + average_score;
					System.out.println(score);
				}
				else{
					score = score + 0.00;
				}
			}
			score = score/nums;
			System.out.println(score);
			if(90<=score&&score<=100)
				score_nums[0]++;
			else if(80<=score&&score<=90)
				score_nums[1]++;
			else if(70<=score&&score<=80)
				score_nums[2]++;
			else if(60<=score&&score<=70)
				score_nums[3]++;
			else if(50<=score&&score<=60)
				score_nums[4]++;
			else if(40<=score&&score<=50)
				score_nums[5]++;
			else if(30<=score&&score<=40)
				score_nums[6]++;
			else if(20<=score&&score<=30)
				score_nums[7]++;
			else if(10<=score&&score<=20)
				score_nums[8]++;
			else if(0<=score&&score<=10)
				score_nums[9]++;
			StudentScore studentScore = new StudentScore();
			studentScore.setS_no(students.get(m).getSNo());
			studentScore.setS_name(students.get(m).getSName());
			studentScore.setS_grade(students.get(m).getSGrade());
			studentScore.setS_class(students.get(m).getMclass().getClassName());
			studentScore.setScore(score);
			studentScores.add(studentScore);
		}
		Map requestMap = (Map) ActionContext.getContext().get("request");
		requestMap.put("score_nums", score_nums);
		requestMap.put("studentScore", studentScores);
		requestMap.put("class_name", class_name);
		requestMap.put("testId",testId);
		return SUCCESS;
	}
	//查看所查学生所有案例的成绩
	public String browseAllCaseScores() throws Exception{
		Double score;
		if(class_name.equals("全部全部")){
			String classesString = iTestService.getOneTest(testId).getClassName();
			String[] classes = classesString.split(",");
			List<StudentAllCaseScore> studentScores = new ArrayList<StudentAllCaseScore>();
			for(int t = 0;t<=classes.length-1;t++) {
				List<Student> students = studentService.getStudentsByClass(classes[t]);
				List<Station> stations = stationService.gainAllStations(testId);
				int[] st_id = new int[stations.size()];
				for (int j = 0; j <= stations.size() - 1; j++) {
					st_id[j] = stations.get(j).getStId();
				}
				int nums = stations.size();
				for (int m = 0; m <= students.size() - 1; m++) {
					score = 0.00;
					for (int n = 0; n <= st_id.length - 1; n++) {
						List<Case> cases = caseService.getCases(st_id[n]);
						for(int k = 0;k<=cases.size()-1;k++) {
							List<Score> scores = scoreService.getScoreBySNoAndCId(students.get(m).getSNo(), cases.get(k).getCId());
							if(scores.size()==0){
								StudentAllCaseScore studentScore = new StudentAllCaseScore();
								studentScore.setSNo(students.get(m).getSNo());
								studentScore.setSName(students.get(m).getSName());
								studentScore.setSGrade(students.get(m).getSGrade());
								studentScore.setSClass(students.get(m).getMclass().getClassName());
								studentScore.setCaseName("未参加"+cases.get(k).getCName()+"案例考试");
								studentScore.setStationName("未参加"+stations.get(n).getStName()+"站考试");
								studentScore.setTName("未参加本案例考试");
								studentScore.setScore(score);
								studentScores.add(studentScore);
							}
							for(int v = 0;v<=scores.size()-1;v++){
								System.out.println(score);
								StudentAllCaseScore studentScore = new StudentAllCaseScore();
								studentScore.setSNo(students.get(m).getSNo());
								studentScore.setSName(students.get(m).getSName());
								studentScore.setSGrade(students.get(m).getSGrade());
								studentScore.setSClass(students.get(m).getMclass().getClassName());
								studentScore.setCaseName(cases.get(k).getCName());
								studentScore.setStationName(stations.get(n).getStName());
								studentScore.setTName(scores.get(v).getTName());
								studentScore.setScore(scores.get(v).getScTotalScore());
								studentScores.add(studentScore);
							}
						}
					}

				}
			}
			Map requestMap = (Map) ActionContext.getContext().get("request");
			requestMap.put("studentScore", studentScores);
			requestMap.put("class_name", class_name);
			requestMap.put("testId",testId);
			return SUCCESS;
		}
			List<StudentAllCaseScore> studentScores = new ArrayList<StudentAllCaseScore>();
			List<Student> students = studentService.getStudentsByClass(class_name);
			List<Station> stations = stationService.gainAllStations(testId);
			int[] st_id = new int[stations.size()];
			for (int j = 0; j <= stations.size() - 1; j++) {
				st_id[j] = stations.get(j).getStId();
			}
			int nums = stations.size();
			for (int m = 0; m <= students.size() - 1; m++) {
				score = 0.00;
				for (int n = 0; n <= st_id.length - 1; n++) {
					List<Case> cases = caseService.getCases(st_id[n]);
					for(int k = 0;k<=cases.size()-1;k++) {
						List<Score> scores = scoreService.getScoreBySNoAndCId(students.get(m).getSNo(), cases.get(k).getCId());
						if(scores.size()==0){
							StudentAllCaseScore studentScore = new StudentAllCaseScore();
							studentScore.setSNo(students.get(m).getSNo());
							studentScore.setSName(students.get(m).getSName());
							studentScore.setSGrade(students.get(m).getSGrade());
							studentScore.setSClass(students.get(m).getMclass().getClassName());
							studentScore.setCaseName("未参加"+cases.get(k).getCName()+"案例考试");
							studentScore.setStationName("未参加"+stations.get(n).getStName()+"站考试");
							studentScore.setTName("未参加本案例考试");
							studentScore.setScore(score);
							studentScores.add(studentScore);
						}
						for(int v = 0;v<=scores.size()-1;v++){
							System.out.println(score);
							StudentAllCaseScore studentScore = new StudentAllCaseScore();
							studentScore.setSNo(students.get(m).getSNo());
							studentScore.setSName(students.get(m).getSName());
							studentScore.setSGrade(students.get(m).getSGrade());
							studentScore.setSClass(students.get(m).getMclass().getClassName());
							studentScore.setCaseName(cases.get(k).getCName());
							studentScore.setStationName(stations.get(n).getStName());
							studentScore.setTName(scores.get(v).getTName());
							studentScore.setScore(scores.get(v).getScTotalScore());
							studentScores.add(studentScore);
						}
					}
				}

			}
		Map requestMap = (Map) ActionContext.getContext().get("request");
		requestMap.put("studentScore", studentScores);
		requestMap.put("class_name", class_name);
		requestMap.put("testId",testId);
		return SUCCESS;
	}
	public String browseStationScores() throws Exception{
		Double score; 
		Double max = 0.00;
		Double min = 100.00;
		Double ave = 0.00;
		int stu_num = 0;
		int[] score_nums = {30,40,20,50,80,70,40,30,10,5};
		List<Student> students = studentService.getAllStudents();
		System.out.print(students.get(0).getSNo());
		System.out.print(stc_id);
		List<StudentScore> studentScores = new ArrayList<StudentScore>();
		for(int m = 0;m<=students.size()-1;m++){
			score = 0.00;	
				List<Score> scores = new ArrayList<Score>();
			    scores = scoreService.gainScore(students.get(m).getSNo(), stc_id);
			
				System.out.print("is running");
				if(scores.size()==1){
					score = scores.get(0).getScTotalScore();
				}
				else if(scores.size()>1){
					Double average_score = 0.00;
					for(int l = 0;l<=scores.size()-1;l++){
						
						average_score = average_score + scores.get(l).getScTotalScore();	
						
					}
					average_score = average_score/scores.size();
					score = average_score;
				}
				else{
					score = 0.00;
				}
			if(90<=score&&score<=100)
				score_nums[0]++;
			else if(80<=score&&score<=90)
				score_nums[1]++;
			else if(70<=score&&score<=80)
				score_nums[2]++;
			else if(60<=score&&score<=70)
				score_nums[3]++;
			else if(50<=score&&score<=60)
				score_nums[4]++;
			else if(40<=score&&score<=50)
				score_nums[5]++;
			else if(30<=score&&score<=40)
				score_nums[6]++;
			else if(20<=score&&score<=30)
				score_nums[7]++;
			else if(10<=score&&score<=20)
				score_nums[8]++;
			else if(0<=score&&score<=10)
				score_nums[9]++;
			if(score>max){
				max = score;
			}
			if(score<min){
				min = score;
			}
			ave = ave + score;
			stu_num++;
			StudentScore studentScore = new StudentScore();
			studentScore.setS_no(students.get(m).getSNo());
			studentScore.setS_name(students.get(m).getSName());
			studentScore.setS_grade(students.get(m).getSGrade());
			studentScore.setS_class(students.get(m).getMclass().getClassName());
			studentScore.setScore(score);
			studentScores.add(studentScore);
		}
		ave = ave/stu_num;
		
		Map requestMap = (Map) ActionContext.getContext().get("request");
		requestMap.put("score_nums", score_nums);
		requestMap.put("studentScore", studentScores);
		requestMap.put("max", max);
		requestMap.put("min", min);
		requestMap.put("ave", ave);
		return SUCCESS;
	}

}


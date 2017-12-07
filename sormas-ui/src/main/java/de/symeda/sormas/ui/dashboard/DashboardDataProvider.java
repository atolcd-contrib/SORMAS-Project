package de.symeda.sormas.ui.dashboard;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.FacadeProvider;
import de.symeda.sormas.api.caze.DashboardCaseDto;
import de.symeda.sormas.api.event.DashboardEventDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.sample.DashboardSampleDto;
import de.symeda.sormas.api.sample.DashboardTestResultDto;
import de.symeda.sormas.api.task.DashboardTaskDto;
import de.symeda.sormas.api.task.TaskStatus;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.EpiWeek;
import de.symeda.sormas.ui.login.LoginHelper;

public class DashboardDataProvider {
	
	private DistrictReferenceDto district;
	private Disease disease;
	private DateFilterOption dateFilterOption;
	private Date fromDate;
	private Date toDate;
	private EpiWeek fromWeek;
	private EpiWeek toWeek;
	
	private List<DashboardCaseDto> cases = new ArrayList<>();
	private List<DashboardCaseDto> previousCases = new ArrayList<>();
	private List<DashboardEventDto> events = new ArrayList<>();
	private List<DashboardEventDto> previousEvents = new ArrayList<>();
	private List<DashboardTestResultDto> testResults = new ArrayList<>();
	private List<DashboardTestResultDto> previousTestResults = new ArrayList<>();
	private List<DashboardSampleDto> samples = new ArrayList<>();
	private List<DashboardTaskDto> tasks = new ArrayList<>();
	private List<DashboardTaskDto> pendingTasks = new ArrayList<>();
	
	public void refreshData() {
		// Update the entities lists according to the filters
		String userUuid = LoginHelper.getCurrentUser().getUuid();
		
		if (dateFilterOption == DateFilterOption.DATE) {
			int period = DateHelper.getDaysBetween(fromDate, toDate);
			Date previousFromDate = DateHelper.subtractDays(fromDate, period);
			Date previousToDate = DateHelper.subtractDays(toDate, period);
			// Cases
			setCases(FacadeProvider.getCaseFacade().getNewCasesForDashboard(district, disease, fromDate, toDate, userUuid));
			setPreviousCases(FacadeProvider.getCaseFacade().getNewCasesForDashboard(district, disease, previousFromDate, previousToDate, userUuid));
			// Events
			setEvents(FacadeProvider.getEventFacade().getNewEventsForDashboard(district, disease, fromDate, toDate, userUuid));
			setPreviousEvents(FacadeProvider.getEventFacade().getNewEventsForDashboard(district, disease, previousFromDate, previousToDate, userUuid));
			// Test results
			setTestResults(FacadeProvider.getSampleTestFacade().getNewTestResultsForDashboard(district, disease, fromDate, toDate, userUuid));
			setPreviousTestResults(FacadeProvider.getSampleTestFacade().getNewTestResultsForDashboard(district, disease, previousFromDate, previousToDate, userUuid));
			// Samples
			setSamples(FacadeProvider.getSampleFacade().getNewSamplesForDashboard(district, disease, fromDate, toDate, userUuid));
		} else {
			int period = toWeek.getWeek() - fromWeek.getWeek() + 1;
			Date epiWeekStart = DateHelper.getEpiWeekStart(fromWeek);
			Date epiWeekEnd = DateHelper.getEpiWeekEnd(toWeek);
			Date previousEpiWeekStart = DateHelper.getEpiWeekStart(DateHelper.getPreviousEpiWeek(fromWeek, period));
			Date previousEpiWeekEnd = DateHelper.getEpiWeekEnd(DateHelper.getPreviousEpiWeek(toWeek, period));
			// Cases
			setCases(FacadeProvider.getCaseFacade().getNewCasesForDashboard(district, disease, epiWeekStart, epiWeekEnd, userUuid));
			setPreviousCases(FacadeProvider.getCaseFacade().getNewCasesForDashboard(district, disease, previousEpiWeekStart, previousEpiWeekEnd, userUuid));
			// Events
			setEvents(FacadeProvider.getEventFacade().getNewEventsForDashboard(district, disease, epiWeekStart, epiWeekEnd, userUuid));
			setPreviousEvents(FacadeProvider.getEventFacade().getNewEventsForDashboard(district, disease, previousEpiWeekStart, previousEpiWeekEnd, userUuid));
			// Test results
			setTestResults(FacadeProvider.getSampleTestFacade().getNewTestResultsForDashboard(district, disease, epiWeekStart, epiWeekEnd, userUuid));
			setPreviousTestResults(FacadeProvider.getSampleTestFacade().getNewTestResultsForDashboard(district, disease, previousEpiWeekStart, previousEpiWeekEnd, userUuid));
			// Samples
			setSamples(FacadeProvider.getSampleFacade().getNewSamplesForDashboard(district, disease, epiWeekStart, epiWeekEnd, userUuid));
		}
		// Tasks
		setTasks(FacadeProvider.getTaskFacade().getAllByUserForDashboard(null, 
				DateHelper.getEpiWeekStart(DateHelper.getEpiWeek(new Date())), DateHelper.getEpiWeekEnd(DateHelper.getEpiWeek(new Date())), userUuid));
		setPendingTasks(FacadeProvider.getTaskFacade().getAllByUserForDashboard(TaskStatus.PENDING, null, null, userUuid));
	}
	
	public List<DashboardCaseDto> getCases() {
		return cases;
	}
	public void setCases(List<DashboardCaseDto> cases) {
		this.cases = cases;
	}
	public List<DashboardCaseDto> getPreviousCases() {
		return previousCases;
	}
	public void setPreviousCases(List<DashboardCaseDto> previousCases) {
		this.previousCases = previousCases;
	}
	public List<DashboardEventDto> getEvents() {
		return events;
	}
	public void setEvents(List<DashboardEventDto> events) {
		this.events = events;
	}
	public List<DashboardEventDto> getPreviousEvents() {
		return previousEvents;
	}
	public void setPreviousEvents(List<DashboardEventDto> previousEvents) {
		this.previousEvents = previousEvents;
	}
	public List<DashboardTestResultDto> getTestResults() {
		return testResults;
	}
	public void setTestResults(List<DashboardTestResultDto> testResults) {
		this.testResults = testResults;
	}
	public List<DashboardTestResultDto> getPreviousTestResults() {
		return previousTestResults;
	}
	public void setPreviousTestResults(List<DashboardTestResultDto> previousTestResults) {
		this.previousTestResults = previousTestResults;
	}
	public List<DashboardSampleDto> getSamples() {
		return samples;
	}
	public void setSamples(List<DashboardSampleDto> samples) {
		this.samples = samples;
	}
	public List<DashboardTaskDto> getTasks() {
		return tasks;
	}
	public void setTasks(List<DashboardTaskDto> tasks) {
		this.tasks = tasks;
	}
	public List<DashboardTaskDto> getPendingTasks() {
		return pendingTasks;
	}
	public void setPendingTasks(List<DashboardTaskDto> pendingTasks) {
		this.pendingTasks = pendingTasks;
	}
	public DistrictReferenceDto getDistrict() {
		return district;
	}
	public void setDistrict(DistrictReferenceDto district) {
		this.district = district;
	}
	public Disease getDisease() {
		return disease;
	}
	public void setDisease(Disease disease) {
		this.disease = disease;
	}
	public DateFilterOption getDateFilterOption() {
		return dateFilterOption;
	}
	public void setDateFilterOption(DateFilterOption dateFilterOption) {
		this.dateFilterOption = dateFilterOption;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public EpiWeek getFromWeek() {
		return fromWeek;
	}
	public void setFromWeek(EpiWeek fromWeek) {
		this.fromWeek = fromWeek;
	}
	public EpiWeek getToWeek() {
		return toWeek;
	}
	public void setToWeek(EpiWeek toWeek) {
		this.toWeek = toWeek;
	}

}
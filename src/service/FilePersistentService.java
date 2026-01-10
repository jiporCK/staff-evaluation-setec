package service;

import model.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;


import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Handles Excel file generation for staff evaluations
 */
public class FilePersistentService {

    private final EvaluationService evaluationService;
    private final StaffService staffService;
    private final PeriodService periodService;
    private final PositionService positionService;
    private final DepartmentService departmentService;
    private final OfficeService officeService;

    public FilePersistentService() {
        this.evaluationService = new EvaluationService();
        this.staffService = new StaffService();
        this.periodService = new PeriodService();
        this.positionService = new PositionService();
        this.departmentService = new DepartmentService();
        this.officeService = new OfficeService();
    }

    // =====================================================
    // TOP 10 EMPLOYEES REPORT
    // =====================================================

    public boolean generateTop10EmployeesReport(
            String filePath, Long periodId, Long companyId) {

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Top 10 Employees");

            CellStyle headerStyle = createHeaderStyle(workbook);

            String[] headers = {
                    "Rank", "Staff ID", "Name",
                    "Position", "Department", "Office",
                    "Average Score"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<AssignStaffEvaluation> evaluations =
                    evaluationService.getAllStaffEvaluations();

            List<StaffScore> topStaff =
                    getTopStaffByPeriod(evaluations, periodId, companyId);

            int rowNum = 1;
            int rank = 1;

            for (StaffScore staffScore : topStaff) {
                if (rank > 10) break;

                Staff staff =
                        staffService.getStaffById(staffScore.staffId);
                if (staff == null) continue;

                Position position = staff.getPositionId() != null
                        ? positionService.getPositionById(staff.getPositionId())
                        : null;

                Department department = staff.getDepartmentId() != null
                        ? departmentService.getDepartmentById(staff.getDepartmentId())
                        : null;

                Office office = staff.getOfficeId() != null
                        ? officeService.getOfficeById(staff.getOfficeId())
                        : null;

                Row row = sheet.createRow(rowNum++);
                int col = 0;

                row.createCell(col++).setCellValue(rank++);
                row.createCell(col++).setCellValue(staff.getId());
                row.createCell(col++).setCellValue(staff.getName());
                row.createCell(col++).setCellValue(position != null ? position.getName() : "N/A");
                row.createCell(col++).setCellValue(department != null ? department.getName() : "N/A");
                row.createCell(col++).setCellValue(office != null ? office.getName() : "N/A");
                row.createCell(col).setCellValue(staffScore.averageScore.doubleValue());
            }

            autoSize(sheet, headers.length);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =====================================================
    // STAFF EVALUATION FORM (UPDATED)
    // =====================================================

    public boolean generateStaffEvaluationForm(
            String filePath, Long evaluationId, Long companyId) {

        try (HSSFWorkbook workbook = new HSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Staff Evaluation Form");

            AssignStaffEvaluation evaluation = getEvaluationById(evaluationId);
            if (evaluation == null) return false;

            Staff staff =
                    staffService.getStaffById(evaluation.getForStaffId());
            if (staff == null) return false;

            Period period =
                    periodService.getPeriodById(evaluation.getPeriodId());

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle labelStyle = createBoldStyle(workbook);

            int rowNum = 0;

            // -------- TITLE --------
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("STAFF EVALUATION FORM");
            titleCell.setCellStyle(createTitleStyle(workbook));

            rowNum++;

            // -------- STAFF INFO --------
            createInfoRow(sheet, rowNum++, "Staff ID", staff.getId().toString(), labelStyle);
            createInfoRow(sheet, rowNum++, "Staff Name", staff.getName(), labelStyle);
            createInfoRow(sheet, rowNum++, "Period",
                    period != null ? period.getCode() : "N/A", labelStyle);
            createInfoRow(sheet, rowNum++, "Evaluation Date",
                    evaluation.getAssignDate().format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    labelStyle);

            rowNum++;

            // -------- SCORES HEADER --------
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                    "No", "Evaluator ID", "Evaluator Name",
                    "Scores", "Average Score", "Last Submitted At"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            List<AssignStaffEvaluationList> evaluators =
                    evaluationService.getEvaluationListItems(evaluationId);

            BigDecimal grandTotal = BigDecimal.ZERO;
            int evaluatorCount = 0;
            int index = 1;

            for (AssignStaffEvaluationList evaluator : evaluators) {

                Row row = sheet.createRow(rowNum++);

                Staff evaluatorStaff =
                        staffService.getStaffById(
                                evaluator.getEvaluationStaffId());

                List<ASEAssignScore> scores =
                        evaluationService.getScoresByEvaluationListId(
                                evaluator.getId());

                row.createCell(0).setCellValue(index++);
                row.createCell(1).setCellValue(evaluator.getEvaluationStaffId());
                row.createCell(2).setCellValue(
                        evaluatorStaff != null
                                ? evaluatorStaff.getName()
                                : "Unknown"
                );

                if (scores.isEmpty()) {
                    row.createCell(3).setCellValue("Not Submitted");
                    row.createCell(4).setCellValue("N/A");
                    row.createCell(5).setCellValue("N/A");
                    continue;
                }

                BigDecimal total = BigDecimal.ZERO;
                StringBuilder scoreText = new StringBuilder();
                ASEAssignScore latest = scores.get(0);

                for (ASEAssignScore s : scores) {
                    total = total.add(s.getScore());
                    scoreText.append(s.getScore()).append(", ");

                    if (s.getCreatedAt().isAfter(latest.getCreatedAt())) {
                        latest = s;
                    }
                }

                BigDecimal avg = total.divide(
                        BigDecimal.valueOf(scores.size()),
                        2,
                        BigDecimal.ROUND_HALF_UP
                );

                row.createCell(3)
                        .setCellValue(scoreText.substring(0, scoreText.length() - 2));
                row.createCell(4).setCellValue(avg.doubleValue());
                row.createCell(5).setCellValue(
                        latest.getCreatedAt().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                );

                grandTotal = grandTotal.add(avg);
                evaluatorCount++;
            }

            rowNum++;

            // -------- FINAL AVERAGE --------
            BigDecimal finalAverage = evaluatorCount > 0
                    ? grandTotal.divide(
                    BigDecimal.valueOf(evaluatorCount),
                    2,
                    BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO;

            Row finalRow = sheet.createRow(rowNum);
            Cell label = finalRow.createCell(0);
            label.setCellValue("FINAL AVERAGE SCORE");
            label.setCellStyle(headerStyle);
            finalRow.createCell(1).setCellValue(finalAverage.doubleValue());

            autoSize(sheet, headers.length);

            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private void createInfoRow(
            Sheet sheet, int rowNum, String label,
            String value, CellStyle labelStyle) {

        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(label);
        cell.setCellStyle(labelStyle);
        row.createCell(1).setCellValue(value);
    }

    private CellStyle createHeaderStyle(HSSFWorkbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle createBoldStyle(HSSFWorkbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle createTitleStyle(HSSFWorkbook wb) {
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        CellStyle style = wb.createCellStyle();
        style.setFont(font);
        return style;
    }

    private void autoSize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private AssignStaffEvaluation getEvaluationById(Long id) {
        for (AssignStaffEvaluation e :
                evaluationService.getAllStaffEvaluations()) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    private List<StaffScore> getTopStaffByPeriod(
            List<AssignStaffEvaluation> evaluations,
            Long periodId, Long companyId) {

        Map<Long, BigDecimal> map = new HashMap<>();

        for (AssignStaffEvaluation e : evaluations) {
            if (!e.getPeriodId().equals(periodId)
                    || !e.getCompanyId().equals(companyId)) continue;

            BigDecimal avg =
                    evaluationService.calculateAverageScore(
                            e.getForStaffId(), periodId);

            if (avg != null) map.put(e.getForStaffId(), avg);
        }

        List<StaffScore> result = new ArrayList<>();
        for (var entry : map.entrySet()) {
            result.add(new StaffScore(entry.getKey(), entry.getValue()));
        }

        result.sort((a, b) ->
                b.averageScore.compareTo(a.averageScore));

        return result;
    }

    private static class StaffScore {
        Long staffId;
        BigDecimal averageScore;

        StaffScore(Long staffId, BigDecimal averageScore) {
            this.staffId = staffId;
            this.averageScore = averageScore;
        }
    }
}

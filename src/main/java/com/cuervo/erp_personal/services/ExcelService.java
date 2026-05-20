package com.cuervo.erp_personal.services;
import com.cuervo.erp_personal.models.Activity;
import com.cuervo.erp_personal.repositories.ActivityRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
@Service
public class ExcelService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> importExcel(String filePath) throws Exception {

        List<Activity> listOfActivities = new ArrayList<>();

        // se abre el archivo Excel mediante un flujo de datos (InputStream)
        FileInputStream file = new FileInputStream(new File(filePath));

        // se crea el objeto Libro (Workbook) que representa el archivo .xlsx completo
        Workbook workbook = new XSSFWorkbook(file);

        // se obtiene la primera hoja del Excel (índice 0)
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue; // Si la fila está vacía se salta

            Activity activity = new Activity();

            // se mapea la Columna 0: Fecha
            Cell cellDate = row.getCell(0);
            if (cellDate != null && cellDate.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cellDate)) {
                    LocalDate date = cellDate.getDateCellValue().toInstant()
                            .atZone(ZoneId.systemDefault()).toLocalDate();
                    activity.setDate(date);
                }
            } else {
                activity.setDate(LocalDate.now()); // Fallback: en caso de dar respuesta alternativa
            }

            // se mapea la Columna 1: Hora (Texto)
            Cell cellTime = row.getCell(1);
            if (cellTime != null) {
                if (cellTime.getCellType() == CellType.NUMERIC) {
                    // Si Excel guardó la hora como un número flotante interno
                    activity.setTime(cellTime.getLocalDateTimeCellValue().toLocalTime().toString());
                } else {
                    activity.setTime(cellTime.getStringCellValue());
                }
            } else {
                activity.setTime("");
            }

            // se mapea la Columna 2: Descripción (Texto)
            Cell cellDesc = row.getCell(2);
            activity.setDescription(cellDesc != null ? cellDesc.getStringCellValue() : "");

            // se mapea la Columna 3: Categoría (Texto)
            Cell cellCat = row.getCell(3);
            activity.setCategory(cellCat != null ? cellCat.getStringCellValue() : "");

            // se mapea la Columna 4: Categoría (Texto)
            Cell cellComment = row.getCell(4);
            activity.setComment(cellComment != null ? cellComment.getStringCellValue() : "");

            // se mapea la Columna 5: Resultado (Entero)
            Cell cellRes = row.getCell(5);
            if (cellRes != null) {
                if (cellRes.getCellType() == CellType.NUMERIC) {
                    // Como en tu captura pones "100" directo, lo casteamos a entero directamente
                    activity.setResult((int) cellRes.getNumericCellValue());
                } else if (cellRes.getCellType() == CellType.STRING) {
                    // Por si acaso hubiera un texto suelto
                    try {
                        activity.setResult(Integer.parseInt(cellRes.getStringCellValue().trim()));
                    } catch (NumberFormatException e) {
                        activity.setResult(0);
                    }
                }
            } else {
                activity.setResult(0);
            }

            // se añade la actividad a nuestra lista temporal
            listOfActivities.add(activity);
        }


        // se cierra para liberar memoria en el sistema operativo
        workbook.close();
        file.close();

        return activityRepository.saveAll(listOfActivities);
    }
}

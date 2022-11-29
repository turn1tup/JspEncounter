package galaxy.jsp.encounter.cfg;

import galaxy.jsp.encounter.xml.Options;
import galaxy.jsp.encounter.util.Utils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Config {


    private static boolean validCell(XSSFCell cell) {
        if (cell == null) {
            return false;
        }
        return cell.getCellStyle().getFont().getBold();
    }

    private static String formatCellValue(String value) {
        if (value.length() > 0 && value.charAt(0)=='`') {
            value = value.substring(1);
        }
        if (value.length() > 0 && value.charAt(value.length()-1)=='`') {
            value = value.substring(0,value.length()-1);
        }
        return value;
    }



    public static Options ParseCfg(InputStream inputStream) throws Exception {


        Options options = new Options();
//
//        InputStream inputStream = Config.class.getResourceAsStream("/matrix.xlsx");

        XSSFWorkbook book = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = book.getSheetAt(0);

        int startRow = -1;
        int startCol = -1;
        for (int rowIndex = 0; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);

            // 定位表格
            if (startRow == -1) {
                for (int colIndex = 0; colIndex < row.getPhysicalNumberOfCells(); colIndex++) {
                    String s = row.getCell(colIndex).getStringCellValue();
                    if ("matrix".equalsIgnoreCase(s)) {
                        startRow = rowIndex + 1;
                        startCol = colIndex;
                        break;
                    }
                }
                continue;
            }


            int cfgIndex = startCol+1;
            if (row == null) {
                continue;
            }
            String cfg = row.getCell(cfgIndex).getStringCellValue();
            if (cfg == null || cfg.length() == 0) {
                continue;
            }
            if ("extTag".equals(cfg)) {
                // 验证有没有加粗

                cfgIndex += 1;
                if (!validCell(row.getCell(cfgIndex))) {
                    continue;
                }
                String tagName = row.getCell(cfgIndex).getStringCellValue();
                int maxColIndex = row.getPhysicalNumberOfCells();
                Map<String, String> map = null;
                String k = null;
                for (int colIndex = cfgIndex+1; colIndex < maxColIndex; colIndex++) {
                    XSSFCell cell = row.getCell(colIndex);
                    if (!validCell(cell)) {
                        continue;
                    }
                    String value = formatCellValue(cell.getStringCellValue());

                    if (map == null) {
                        map = new HashMap<>();
                    }
                    if (k == null) {
                        k = value;
                    }else{
                        map.put(k, value);
                        k = null;
                    }
                }
                options.extTags.add(new ImmutablePair<>(tagName, map));
            }else{
                Type type = Utils.GetFieldType(options, cfg);

                int maxColIndex = row.getPhysicalNumberOfCells();
                Map<String, String> map = null;
                String k = null;
                for (int colIndex = cfgIndex+1; colIndex < maxColIndex; colIndex++) {
                    XSSFCell cell = row.getCell(colIndex);
                    if (!validCell(cell)) {
                        continue;
                    }
                    String value = formatCellValue(cell.getStringCellValue());
                    if (type == String.class) {
                        Utils.SetFieldValue(options, cfg, value);
                    } else if (type == boolean.class) {
                        Utils.SetFieldValue(options,cfg, "true".equalsIgnoreCase(value));
                    } else if (type == int.class) {
                        Utils.SetFieldValue(options, cfg, Integer.valueOf(value));
                    } else if ("java.util.Map<java.lang.String, java.lang.String>".equals(type.getTypeName()) ) {
                        if (map == null) {
                            map = new HashMap<>();
                        }
                        if (k == null) {
                            k = value;
                        }else{
                            map.put(k, value);
                            k = null;
                        }
                    }

                }
                if ("java.util.Map<java.lang.String, java.lang.String>".equals(type.getTypeName())) {
                    Utils.SetFieldValue(options, cfg, map);
                }
            }

        }
        //System.out.println(options);
        return options;

    }
}

package logic.excel;

import org.apache.poi.ss.usermodel.Workbook;

public class CreatorXLS {

    Workbook wb;

    public CreatorXLS(Workbook wb) {
        this.wb = wb;
    }

    public Workbook getWb() {
        return wb;
    }

    public void setWb(Workbook wb) {
        this.wb = wb;
    }
}

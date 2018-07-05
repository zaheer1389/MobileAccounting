package com.adslinfotech.mobileaccounting.database;

public class Query {
    public static final String ACCOUNT_ALL = "SELECT rowid _id,* FROM Account ORDER BY PersonName COLLATE NOCASE";
    public static final String ACCOUNT_LIST_HOME = "SELECT rowid _id,*, (select (Sum(Transection.Credit_Amount) - Sum(Transection.Debit_Amount)) FROM Transection where Transection.AID = Account.AID) AS bal FROM Account ORDER BY PersonName COLLATE NOCASE";

    public interface ACCOUNT {
        public static final String NAME = "PersonName";
    }

    public static final String getAccountHomeSearch(String str) {
        return "SELECT rowid _id,*, (select (Sum(Transection.Credit_Amount) - Sum(Transection.Debit_Amount)) FROM Transection where Transection.AID = Account.AID) AS bal FROM Account WHERE PersonName LIKE '%" + str + "%' OR PersonEmail LIKE '%" + str + "%' ORDER BY PersonName COLLATE NOCASE";
    }
}

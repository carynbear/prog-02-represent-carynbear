package com.example.ngan.prog2b;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by ngan on 3/2/16.
 * Holds the data from the Representative ListView Item
 */
public class RepListItem {
    public final Drawable pic;
    public final String party;

    public final String name;
    public final String title;
    public final String email;
    public final String web;
    public final String tweet;
    public final String term = "1995 to 2016";
    public final String[] committees = {
            "Armed Services",
            "Budget",
            "Education and the Workforce",
            "Energy and Commerce",
            };
    public final String[] billTitles = {
            "S. 2054: Justice is Not For Sale Act of 2015",
            "S. 2023: Prescription Drug Affordability Act of 2015",
            "S. 1969: Democracy Day Act of 2015",
            "S. 1970: Raising Enrollment with a Government Initiated System " +
                    "for Timely Electoral Registration (REGISTER) Act of 2015 ",
            "S. 1832: Pay Workers a Living Wage Act"};

    public final String[] billDates = {
            "Sep 17, 2015",
            "Sep 10, 2015",
            "Aug 5, 2015",
            "Aug 5, 2015",
            "Jul 22, 2015" };
    public final ArrayList<Bill> bills = new ArrayList<Bill>();


    public RepListItem(Drawable pic, String party, String name,
                       String title, String email, String website, String tweet) {
        this.pic = pic;
        this.party = party;
        this.name = name;
        this.title = title;
        this.email = email;
        this.web = website;
        this.tweet = tweet;

        for(int i = 0; i < billTitles.length; i++) {
            Bill added = new Bill(billTitles[i], billDates[i]);
            bills.add(added);
        }
    }

    public String getCommittees(){
        String ans = "";
        String new_line = "\n";
        for (String c : committees) {
            ans += c;
            ans += new_line;
        }
        return ans;
    }

    public String getParty(){
        if (party.equals("R")) {
            return "Republican Party";
        } else return "Democratic Party";
    }

    public String getBills(){
        String ans = "";
        String date_indent = "    ";
        String new_line = "\n";
        String open = " [";
        String close = "] ";
        for (Bill b : bills) {
            ans += b.getTitle();
            ans += new_line;
            ans += date_indent;
            ans += open;
            ans += b.getDate();
            ans += close;
            ans += new_line;
        }
        return ans;
    }

    public class Bill {
        private final String title;
        private final String date;

        public Bill(String t, String d) {
            title = t;
            date = d;
        }

        public String getTitle() {
            return title;
        }

        public String getDate() {
            return date;
        }
    }
}

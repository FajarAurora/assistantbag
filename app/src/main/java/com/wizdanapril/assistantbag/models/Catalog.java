package com.wizdanapril.assistantbag.models;

import java.util.Map;

public class Catalog {

    public String id;
    public String name;
    public String status;
    public String lastReadDate;
    public String lastReadTime;
    public String lastReadDay;
    public Map<String, Boolean> schedule;
//    public Schedule schedule;

    public Catalog() {

    }

    public Catalog(String id, String name, String status, String lastReadDate,
                   String lastReadTime, String lastReadDay,
                   Map<String, Boolean> schedule
    ) {

        this.id = id;
        this.name = name;
        this.status = status;
        this.lastReadDate = lastReadDate;
        this.lastReadTime = lastReadTime;
        this.lastReadDay = lastReadDay;
        this.schedule = schedule;

    }

//    public String getId() {
//        return id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public String getLastReadDate() {
//        return lastReadDate;
//    }
//
//    public String getLastReadTime() {
//        return lastReadTime;
//    }
//
//    public String getLastReadDay() {
//        return lastReadDay;
//    }
//
//    public Map<String, Boolean> getSchedule() {
//        return schedule;
//    }

//    public class Schedule {
//        boolean Mon;
//        boolean Tue;
//        boolean Wed;
//        boolean Thu;
//        boolean Fri;
//        boolean Sat;
//        boolean Sun;
//
//        public Schedule() {
//        }
//
//        public Schedule(boolean mon, boolean tue, boolean wed, boolean thu,
//                        boolean fri, boolean sat, boolean sun) {
//            Mon = mon;
//            Tue = tue;
//            Wed = wed;
//            Thu = thu;
//            Fri = fri;
//            Sat = sat;
//            Sun = sun;
//        }
//
//        public boolean isMon() {
//            return Mon;
//        }
//
//        public boolean isTue() {
//            return Tue;
//        }
//
//        public boolean isWed() {
//            return Wed;
//        }
//
//        public boolean isThu() {
//            return Thu;
//        }
//
//        public boolean isFri() {
//            return Fri;
//        }
//
//        public boolean isSat() {
//            return Sat;
//        }
//
//        public boolean isSun() {
//            return Sun;
//        }
//    }

    //
//    public Map<String, Boolean> getSchedule() {
//        return schedule;
//    }

//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("id", id);
//        result.put("name", name);
//        result.put("status", status);
//        result.put("lastReadDate", lastReadDate);
//        result.put("lastReadTime", lastReadTime);
//        result.put("lastReadDay", lastReadDay);
//        result.put("schedule", schedule);
//
//        return result;
//    }
//
//    public Map<String, Boolean> toSchedule() {
//        HashMap<String, Boolean> result = new HashMap<>();
//        for (Map.Entry<String, Boolean>)
//        result.get("Sun");
//        result.get("Mon");
//        result.get("Tue");
//        result.get("Wed");
//        result.get("Thu");
//        result.get("Fri");
//        result.get("Sat");
//        result.put("Sun", schedule.get("Mon"));
//        result.put("Mon", schedule.get("Mon"));
//        result.put("Tue", schedule.get("Tue"));
//        result.put("Wed", schedule.get("Wed"));
//        result.put("Thu", schedule.get("Thu"));
//        result.put("Fri", schedule.get("Fri"));
//        result.put("Sat", schedule.get("Sat"));
//
//        return result;
//    }
}
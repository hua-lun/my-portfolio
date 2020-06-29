// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.List;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

        // Get attendees and duration
        Collection<String> attendees = request.getAttendees();
        long duration = request.getDuration();

        // Create list to store available timings
        Collection<TimeRange> availableTimings = new ArrayList<>();

        // if there are no attendees, return the whole day
        if (attendees.isEmpty()) {
            availableTimings.add(TimeRange.WHOLE_DAY);
            return availableTimings;
        }

        // if the duration is longer than a day, return no timings (empty list)
        if (duration >= TimeRange.WHOLE_DAY.duration() + 1) {
            return availableTimings;
        }

        // get unavailable event timings
        List<Event> unavailableTimings = getUnavailableTimings(events, attendees);

        // get available event timings
        availableTimings = getAvailableTimings(unavailableTimings, duration);

        return availableTimings;
    }

    // Get unavailable timings for events, sort events and return the list of timings
    private List<Event> getUnavailableTimings(Collection<Event> events, Collection<String> attendees) {
        List<Event> unavailableTimings = new ArrayList<>();
        Consumer<Event> consumer = s -> {
            for (String a : attendees) {
                if (s.getAttendees().contains(a)) {
                    unavailableTimings.add(s);
                    break;
                }
            }
        };
        events.forEach(consumer);
        
        Collections.sort(unavailableTimings, TimeRange.ORDER_BY_START);
        return unavailableTimings;
    }

    // Get TimeRange of available timings
    private Collection<TimeRange> getAvailableTimings(List<Event> unavailableTimings, long duration) {
        Collection<TimeRange> timeRangeList = new ArrayList<>();

        int startTime = TimeRange.START_OF_DAY;
        int closingTime = TimeRange.END_OF_DAY;

        for (Event e : unavailableTimings) {
            TimeRange timeRange = e.getWhen();

            // check if event can be added before the start time
            if (startTime + duration <= timeRange.start()) {
                TimeRange timeAvailableBeforeEvent = TimeRange.fromStartEnd(startTime, timeRange.start(), false);
                timeRangeList.add(timeAvailableBeforeEvent);
                // since event is added, the start time == end time
                startTime = timeRange.end();

                // if there is not enough time, change start time == end time
            } else if (startTime < timeRange.end()) {
                startTime = timeRange.end();
            }

        }
        // check if an event can be added in before closing time
        if (closingTime - startTime >= duration) {
            TimeRange timeAvailableBeforeClosing = TimeRange.fromStartEnd(startTime, TimeRange.END_OF_DAY, true);
            timeRangeList.add(timeAvailableBeforeClosing);
        }
        return timeRangeList;
    }

}

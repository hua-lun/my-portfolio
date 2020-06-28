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
import java.util.List;

public final class FindQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

        // Get attendees and duration
        Collection<String> attendees = request.getAttendees();
        long duration = request.getDuration();

        // Create list to store available timings
        Collection<TimeRange> times = new ArrayList<>();

        // if there are no attendees, return the whole day
        if (attendees.isEmpty()) {
            times.add(TimeRange.WHOLE_DAY);
            return times;
        }

        // if the duration is longer than a day, return no timings (empty list)
        if (duration >= TimeRange.WHOLE_DAY.duration() + 1) {
            return times;
        }

        // get unavailable event timings
        List<Event> unavailableTimings = getUnavailableTimings(events, attendees);

        // get available event timings
        times = getAvailableTimings(unavailableTimings, duration);

        return times;
    }

    // Get unavailable timings for events, sort events and return the list of timings
    private List<Event> getUnavailableTimings(Collection<Event> events, Collection<String> attendees) {
        List<Event> unavailableTimings = new ArrayList<>();
        for (Event e : events) {
            for (String a : attendees) {
                if (e.getAttendees().contains(a)) {
                    unavailableTimings.add(e);
                    break;
                }
            }
        }

        Comparator<Event> compareStartingTime =
                (Event e1, Event e2) -> TimeRange.ORDER_BY_START.compare(e1.getWhen(), e2.getWhen());
        Collections.sort(unavailableTimings, compareStartingTime);

        return unavailableTimings;
    }

    // Get TimeRange of available timings
    private Collection<TimeRange> getAvailableTimings(List<Event> unavailableTimings, long duration) {
        Collection<TimeRange> times = new ArrayList<>();

        int startTime = TimeRange.START_OF_DAY;
        int closingTime = TimeRange.END_OF_DAY;

        for (Event e : unavailableTimings) {
            TimeRange tr = e.getWhen();

            // check if event can be added before the start time
            if (startTime + duration <= tr.start()) {
                TimeRange trBefore = TimeRange.fromStartEnd(startTime, tr.start(), false);
                times.add(trBefore);
                // since event is added, the start time == end time
                startTime = tr.end();

                // if there is not enough time, change start time == end time
            } else if (startTime < tr.end()) {
                startTime = tr.end();
            }

        }
        // check if an event can be added in before closing time
        if (closingTime - startTime >= duration) {
            TimeRange trAfter = TimeRange.fromStartEnd(startTime, TimeRange.END_OF_DAY, true);
            times.add(trAfter);
        }
        return times;
    }

}

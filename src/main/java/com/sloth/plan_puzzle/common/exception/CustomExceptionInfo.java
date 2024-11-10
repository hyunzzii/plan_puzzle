package com.sloth.plan_puzzle.common.exception;

import lombok.Getter;

@Getter
public enum CustomExceptionInfo {
    //400
    INVALID_SCHEDULE_TIME(400, "invalid schedule date time", 400001),
    INVALID_IMG_URL(400, "invalid img url", 4000002),
    INVALID_RECRUIT_CAPACITY(400, "participation should be bigger than current recruit count", 400003),
    INVALID_TIME_FORMAT(400, "invalid time format", 400004),
    INVALID_TIMESLOT(400, "end time cannot be before the start time", 400005),
    INVALID_DURATION(400, "duration for generate is not valid", 400006),
    OVERLAPPING_SCHEDULE(400, "this is an overlapping schedule", 400007),
    RECRUITMENT_CLOSED(400, "cannot participate because the recruitment is closed", 400008),
    INVALID_DEADLINE(400, "deadline should be later now", 400009),
    VOTE_ALREADY_EXISTS(400, "vote already exists, can not create vote", 400010),
    ALREADY_FILLED(400, "recruitment have been already filled", 400008),

    //401
    //jwt
    INVALID_ACCESS_TOKEN(401, "invalid access token", 401001),
    EXPIRED_ACCESS_TOKEN(401, "expired access token", 401002),
    INVALID_REFRESH_TOKEN(401, "invalid refresh token", 401003),
    INVALID_PASSWORD(401, "wrong password", 401004),

    //403
    UNAUTHORIZED_ACCESS(403, "not permission", 403001),

    //404
    NOT_FOUND_USER(404, "user not found", 404001),
    NOT_FOUND_SCHEDULE(404, "schedule not found", 404002),
    NOT_FOUND_CHANNEL(404, "channel not found", 404003),
    SUBSCRIPTION_NOT_EXIST(404, "subscription not exist", 404004),
    NOT_FOUND_RECRUITMENT(404, "recruitment not found", 404005),
    PARTICIPATION_NOT_EXIST(404, "participation not exist", 404006),
    NOT_FOUND_VOTE(404, "vote not found", 404007),
    NOT_FOUND_TIMESLOT(404, "time-slot not found", 404008),
    SUBSCRIPTION_ALREADY_EXIST(404, "subscription already exist", 404009),

    //409
    DUPLICATE_ID(409, "id already exists", 409001),
    DUPLICATE_NICKNAME(409, "nickname already exists", 409002);

    private final int statusCode;
    private final String message;
    private final int detailStatusCode;

    CustomExceptionInfo(int statusCode, String message, int detailStatusCode) {
        this.statusCode = statusCode;
        this.message = message;
        this.detailStatusCode = detailStatusCode;
    }
}

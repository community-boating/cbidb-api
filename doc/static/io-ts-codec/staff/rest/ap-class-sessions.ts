import * as t from 'io-ts';
import APIWrapper from 'core/APIWrapper';
import { HttpMethod } from "core/HttpMethod";
import { OptionalNumber, OptionalString } from 'util/OptionalTypeValidators';

const apClassWaitListValidator = t.type({
	"wlResult": "E",
	"foVmDatetime": null,
	"offerExpDatetime": null,
	"signupId": 2104,
	"foAlertDatetime": null
})

const apClassSignupValidator = t.type({
    instanceId: t.number,
    discountInstanceId: OptionalNumber,
    voidedOnline: t.boolean,
    $$apClassWaitlistResult: apClassWaitListValidator,
    personId: t.number,
    orderId: OptionalNumber,
    price: OptionalNumber,
    signupId:  t.number4,
    $$person: t.type({
        personId:  t.number,
        nameFirst: OptionalString,
        nameLast: OptionalString
    }),
    closeId: OptionalNumber,
    sequence: t.number,
    paymentMedium: OptionalString,
    ccTransNum: OptionalString,
    paymentLocation: OptionalString,
    voidCloseId: OptionalNumber,
    signupType: t.string,
    signupNote: t.string,
    signupDatetime: t.string
})

const apClassInstanceValidator = t.type({
	"instanceId": t.number,
	"cancelledDatetime": OptionalString,
	"signupsStartOverride": OptionalString,
	"signupMin": OptionalNumber,
	"price": OptionalNumber,
	"signupMax": OptionalNumber,
	"formatId": t.number,
	"hideOnline": t.boolean,
	"cancelByOverride": OptionalString,
	"locationString": OptionalString,
	$$apClassSignups: t.array(apClassSignupValidator)
})

const apClassSessionValidator = t.type({
	"instanceId": t.number,
	"sessionId": t.number,
	"cancelledDateTime": OptionalString,
	"sessionDateTime": t.string,
	"headcount": OptionalNumber,
	"sessionLength": t.number,
	"$$apClassInstance": apClassInstanceValidator
})


export type ApClassInstance = t.TypeOf<typeof apClassInstanceValidator>
export type ApClassSession = t.TypeOf<typeof apClassSessionValidator>

const path = "/rest/ap-class-sessions/today"

export const postWrapper = new APIWrapper({
	path: path,
	type: HttpMethod.GET,
	resultValidator: t.array(apClassSessionValidator),
})

import * as t from 'io-ts';
import APIWrapper from 'core/APIWrapper';
import { HttpMethod } from "core/HttpMethod";
import { OptionalNumber, OptionalString } from 'util/OptionalTypeValidators';

const apClassTypeValidator = t.type({
	"signupMinDefault": OptionalNumber,
	"noSignup": t.boolean,
	"disallowIfOverkill": t.boolean,
	"signupMaxDefault": OptionalNumber,
	"typeId": t.number,
	"typeName": t.string,
	"priceDefault": OptionalNumber,
	"displayOrder": t.number
});

const apClassFormatValidator = t.type({
	"sessionLengthDefault": t.number,
	"typeId": t.number,
	"signupMinDefaultOverride": OptionalNumber,
	"signupMaxDefaultOverride": OptionalNumber,
	"formatId": t.number,
	"priceDefaultOverride": OptionalNumber,
	"sessionCtDefault": t.number,
	"$$apClassType": apClassTypeValidator
});

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
	"$$apClassFormat": apClassFormatValidator
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


export type ApClassType = t.TypeOf<typeof apClassTypeValidator>
export type ApClassFormat = t.TypeOf<typeof apClassFormatValidator>
export type ApClassInstance = t.TypeOf<typeof apClassInstanceValidator>
export type ApClassSession = t.TypeOf<typeof apClassSessionValidator>

const path = "/rest/ap-class-sessions/today"

export const postWrapper = new APIWrapper({
	path: path,
	type: HttpMethod.GET,
	resultValidator: t.array(apClassSessionValidator),
})

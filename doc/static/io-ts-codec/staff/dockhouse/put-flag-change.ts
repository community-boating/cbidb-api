import * as t from 'io-ts';
import APIWrapper from 'core/APIWrapper';
import { HttpMethod } from "core/HttpMethod";

const putFlagChangeValidator = t.type({
	flagColor: t.string,
})

const path = "/staff/dockhouse/put-flag-change"

export const postWrapper = new APIWrapper({
	path: path,
	type: HttpMethod.POST,
	resultValidator: t.any,
	postBodyValidator: putFlagChangeValidator
})
/*eslint-disable block-scoped-var, id-length, no-control-regex, no-magic-numbers, no-prototype-builtins, no-redeclare, no-shadow, no-var, sort-vars*/
import * as $protobuf from "protobufjs/minimal";

// Common aliases
const $Reader = $protobuf.Reader, $Writer = $protobuf.Writer, $util = $protobuf.util;

// Exported root namespace
const $root = $protobuf.roots["default"] || ($protobuf.roots["default"] = {});

export const de = $root.de = (() => {

    /**
     * Namespace de.
     * @exports de
     * @namespace
     */
    const de = {};

    de.fll = (function() {

        /**
         * Namespace fll.
         * @memberof de
         * @namespace
         */
        const fll = {};

        fll.core = (function() {

            /**
             * Namespace core.
             * @memberof de.fll
             * @namespace
             */
            const core = {};

            core.proto = (function() {

                /**
                 * Namespace proto.
                 * @memberof de.fll.core
                 * @namespace
                 */
                const proto = {};

                proto.LoginRequest = (function() {

                    /**
                     * Properties of a LoginRequest.
                     * @memberof de.fll.core.proto
                     * @interface ILoginRequest
                     * @property {string|null} [username] LoginRequest username
                     * @property {string|null} [password] LoginRequest password
                     */

                    /**
                     * Constructs a new LoginRequest.
                     * @memberof de.fll.core.proto
                     * @classdesc Represents a LoginRequest.
                     * @implements ILoginRequest
                     * @constructor
                     * @param {de.fll.core.proto.ILoginRequest=} [properties] Properties to set
                     */
                    function LoginRequest(properties) {
                        if (properties)
                            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * LoginRequest username.
                     * @member {string} username
                     * @memberof de.fll.core.proto.LoginRequest
                     * @instance
                     */
                    LoginRequest.prototype.username = "";

                    /**
                     * LoginRequest password.
                     * @member {string} password
                     * @memberof de.fll.core.proto.LoginRequest
                     * @instance
                     */
                    LoginRequest.prototype.password = "";

                    /**
                     * Creates a new LoginRequest instance using the specified properties.
                     * @function create
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {de.fll.core.proto.ILoginRequest=} [properties] Properties to set
                     * @returns {de.fll.core.proto.LoginRequest} LoginRequest instance
                     */
                    LoginRequest.create = function create(properties) {
                        return new LoginRequest(properties);
                    };

                    /**
                     * Encodes the specified LoginRequest message. Does not implicitly {@link de.fll.core.proto.LoginRequest.verify|verify} messages.
                     * @function encode
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {de.fll.core.proto.ILoginRequest} message LoginRequest message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    LoginRequest.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.username != null && Object.hasOwnProperty.call(message, "username"))
                            writer.uint32(/* id 1, wireType 2 =*/10).string(message.username);
                        if (message.password != null && Object.hasOwnProperty.call(message, "password"))
                            writer.uint32(/* id 2, wireType 2 =*/18).string(message.password);
                        return writer;
                    };

                    /**
                     * Encodes the specified LoginRequest message, length delimited. Does not implicitly {@link de.fll.core.proto.LoginRequest.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {de.fll.core.proto.ILoginRequest} message LoginRequest message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    LoginRequest.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a LoginRequest message from the specified reader or buffer.
                     * @function decode
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {de.fll.core.proto.LoginRequest} LoginRequest
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    LoginRequest.decode = function decode(reader, length, error) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.de.fll.core.proto.LoginRequest();
                        while (reader.pos < end) {
                            let tag = reader.uint32();
                            if (tag === error)
                                break;
                            switch (tag >>> 3) {
                            case 1: {
                                    message.username = reader.string();
                                    break;
                                }
                            case 2: {
                                    message.password = reader.string();
                                    break;
                                }
                            default:
                                reader.skipType(tag & 7);
                                break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a LoginRequest message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {de.fll.core.proto.LoginRequest} LoginRequest
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    LoginRequest.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a LoginRequest message.
                     * @function verify
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    LoginRequest.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.username != null && message.hasOwnProperty("username"))
                            if (!$util.isString(message.username))
                                return "username: string expected";
                        if (message.password != null && message.hasOwnProperty("password"))
                            if (!$util.isString(message.password))
                                return "password: string expected";
                        return null;
                    };

                    /**
                     * Creates a LoginRequest message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {de.fll.core.proto.LoginRequest} LoginRequest
                     */
                    LoginRequest.fromObject = function fromObject(object) {
                        if (object instanceof $root.de.fll.core.proto.LoginRequest)
                            return object;
                        let message = new $root.de.fll.core.proto.LoginRequest();
                        if (object.username != null)
                            message.username = String(object.username);
                        if (object.password != null)
                            message.password = String(object.password);
                        return message;
                    };

                    /**
                     * Creates a plain object from a LoginRequest message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {de.fll.core.proto.LoginRequest} message LoginRequest
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    LoginRequest.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        let object = {};
                        if (options.defaults) {
                            object.username = "";
                            object.password = "";
                        }
                        if (message.username != null && message.hasOwnProperty("username"))
                            object.username = message.username;
                        if (message.password != null && message.hasOwnProperty("password"))
                            object.password = message.password;
                        return object;
                    };

                    /**
                     * Converts this LoginRequest to JSON.
                     * @function toJSON
                     * @memberof de.fll.core.proto.LoginRequest
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    LoginRequest.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    /**
                     * Gets the default type url for LoginRequest
                     * @function getTypeUrl
                     * @memberof de.fll.core.proto.LoginRequest
                     * @static
                     * @param {string} [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns {string} The default type url
                     */
                    LoginRequest.getTypeUrl = function getTypeUrl(typeUrlPrefix) {
                        if (typeUrlPrefix === undefined) {
                            typeUrlPrefix = "type.googleapis.com";
                        }
                        return typeUrlPrefix + "/de.fll.core.proto.LoginRequest";
                    };

                    return LoginRequest;
                })();

                proto.LoginResponse = (function() {

                    /**
                     * Properties of a LoginResponse.
                     * @memberof de.fll.core.proto
                     * @interface ILoginResponse
                     * @property {boolean|null} [success] LoginResponse success
                     * @property {string|null} [token] LoginResponse token
                     * @property {string|null} [error] LoginResponse error
                     * @property {de.fll.core.proto.IUser|null} [user] LoginResponse user
                     */

                    /**
                     * Constructs a new LoginResponse.
                     * @memberof de.fll.core.proto
                     * @classdesc Represents a LoginResponse.
                     * @implements ILoginResponse
                     * @constructor
                     * @param {de.fll.core.proto.ILoginResponse=} [properties] Properties to set
                     */
                    function LoginResponse(properties) {
                        if (properties)
                            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * LoginResponse success.
                     * @member {boolean} success
                     * @memberof de.fll.core.proto.LoginResponse
                     * @instance
                     */
                    LoginResponse.prototype.success = false;

                    /**
                     * LoginResponse token.
                     * @member {string} token
                     * @memberof de.fll.core.proto.LoginResponse
                     * @instance
                     */
                    LoginResponse.prototype.token = "";

                    /**
                     * LoginResponse error.
                     * @member {string} error
                     * @memberof de.fll.core.proto.LoginResponse
                     * @instance
                     */
                    LoginResponse.prototype.error = "";

                    /**
                     * LoginResponse user.
                     * @member {de.fll.core.proto.IUser|null|undefined} user
                     * @memberof de.fll.core.proto.LoginResponse
                     * @instance
                     */
                    LoginResponse.prototype.user = null;

                    /**
                     * Creates a new LoginResponse instance using the specified properties.
                     * @function create
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {de.fll.core.proto.ILoginResponse=} [properties] Properties to set
                     * @returns {de.fll.core.proto.LoginResponse} LoginResponse instance
                     */
                    LoginResponse.create = function create(properties) {
                        return new LoginResponse(properties);
                    };

                    /**
                     * Encodes the specified LoginResponse message. Does not implicitly {@link de.fll.core.proto.LoginResponse.verify|verify} messages.
                     * @function encode
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {de.fll.core.proto.ILoginResponse} message LoginResponse message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    LoginResponse.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.success != null && Object.hasOwnProperty.call(message, "success"))
                            writer.uint32(/* id 1, wireType 0 =*/8).bool(message.success);
                        if (message.token != null && Object.hasOwnProperty.call(message, "token"))
                            writer.uint32(/* id 2, wireType 2 =*/18).string(message.token);
                        if (message.error != null && Object.hasOwnProperty.call(message, "error"))
                            writer.uint32(/* id 3, wireType 2 =*/26).string(message.error);
                        if (message.user != null && Object.hasOwnProperty.call(message, "user"))
                            $root.de.fll.core.proto.User.encode(message.user, writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
                        return writer;
                    };

                    /**
                     * Encodes the specified LoginResponse message, length delimited. Does not implicitly {@link de.fll.core.proto.LoginResponse.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {de.fll.core.proto.ILoginResponse} message LoginResponse message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    LoginResponse.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a LoginResponse message from the specified reader or buffer.
                     * @function decode
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {de.fll.core.proto.LoginResponse} LoginResponse
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    LoginResponse.decode = function decode(reader, length, error) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.de.fll.core.proto.LoginResponse();
                        while (reader.pos < end) {
                            let tag = reader.uint32();
                            if (tag === error)
                                break;
                            switch (tag >>> 3) {
                            case 1: {
                                    message.success = reader.bool();
                                    break;
                                }
                            case 2: {
                                    message.token = reader.string();
                                    break;
                                }
                            case 3: {
                                    message.error = reader.string();
                                    break;
                                }
                            case 4: {
                                    message.user = $root.de.fll.core.proto.User.decode(reader, reader.uint32());
                                    break;
                                }
                            default:
                                reader.skipType(tag & 7);
                                break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a LoginResponse message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {de.fll.core.proto.LoginResponse} LoginResponse
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    LoginResponse.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a LoginResponse message.
                     * @function verify
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    LoginResponse.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.success != null && message.hasOwnProperty("success"))
                            if (typeof message.success !== "boolean")
                                return "success: boolean expected";
                        if (message.token != null && message.hasOwnProperty("token"))
                            if (!$util.isString(message.token))
                                return "token: string expected";
                        if (message.error != null && message.hasOwnProperty("error"))
                            if (!$util.isString(message.error))
                                return "error: string expected";
                        if (message.user != null && message.hasOwnProperty("user")) {
                            let error = $root.de.fll.core.proto.User.verify(message.user);
                            if (error)
                                return "user." + error;
                        }
                        return null;
                    };

                    /**
                     * Creates a LoginResponse message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {de.fll.core.proto.LoginResponse} LoginResponse
                     */
                    LoginResponse.fromObject = function fromObject(object) {
                        if (object instanceof $root.de.fll.core.proto.LoginResponse)
                            return object;
                        let message = new $root.de.fll.core.proto.LoginResponse();
                        if (object.success != null)
                            message.success = Boolean(object.success);
                        if (object.token != null)
                            message.token = String(object.token);
                        if (object.error != null)
                            message.error = String(object.error);
                        if (object.user != null) {
                            if (typeof object.user !== "object")
                                throw TypeError(".de.fll.core.proto.LoginResponse.user: object expected");
                            message.user = $root.de.fll.core.proto.User.fromObject(object.user);
                        }
                        return message;
                    };

                    /**
                     * Creates a plain object from a LoginResponse message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {de.fll.core.proto.LoginResponse} message LoginResponse
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    LoginResponse.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        let object = {};
                        if (options.defaults) {
                            object.success = false;
                            object.token = "";
                            object.error = "";
                            object.user = null;
                        }
                        if (message.success != null && message.hasOwnProperty("success"))
                            object.success = message.success;
                        if (message.token != null && message.hasOwnProperty("token"))
                            object.token = message.token;
                        if (message.error != null && message.hasOwnProperty("error"))
                            object.error = message.error;
                        if (message.user != null && message.hasOwnProperty("user"))
                            object.user = $root.de.fll.core.proto.User.toObject(message.user, options);
                        return object;
                    };

                    /**
                     * Converts this LoginResponse to JSON.
                     * @function toJSON
                     * @memberof de.fll.core.proto.LoginResponse
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    LoginResponse.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    /**
                     * Gets the default type url for LoginResponse
                     * @function getTypeUrl
                     * @memberof de.fll.core.proto.LoginResponse
                     * @static
                     * @param {string} [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns {string} The default type url
                     */
                    LoginResponse.getTypeUrl = function getTypeUrl(typeUrlPrefix) {
                        if (typeUrlPrefix === undefined) {
                            typeUrlPrefix = "type.googleapis.com";
                        }
                        return typeUrlPrefix + "/de.fll.core.proto.LoginResponse";
                    };

                    return LoginResponse;
                })();

                proto.SessionResponse = (function() {

                    /**
                     * Properties of a SessionResponse.
                     * @memberof de.fll.core.proto
                     * @interface ISessionResponse
                     * @property {boolean|null} [valid] SessionResponse valid
                     * @property {de.fll.core.proto.IUser|null} [user] SessionResponse user
                     * @property {string|null} [error] SessionResponse error
                     */

                    /**
                     * Constructs a new SessionResponse.
                     * @memberof de.fll.core.proto
                     * @classdesc Represents a SessionResponse.
                     * @implements ISessionResponse
                     * @constructor
                     * @param {de.fll.core.proto.ISessionResponse=} [properties] Properties to set
                     */
                    function SessionResponse(properties) {
                        if (properties)
                            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * SessionResponse valid.
                     * @member {boolean} valid
                     * @memberof de.fll.core.proto.SessionResponse
                     * @instance
                     */
                    SessionResponse.prototype.valid = false;

                    /**
                     * SessionResponse user.
                     * @member {de.fll.core.proto.IUser|null|undefined} user
                     * @memberof de.fll.core.proto.SessionResponse
                     * @instance
                     */
                    SessionResponse.prototype.user = null;

                    /**
                     * SessionResponse error.
                     * @member {string} error
                     * @memberof de.fll.core.proto.SessionResponse
                     * @instance
                     */
                    SessionResponse.prototype.error = "";

                    /**
                     * Creates a new SessionResponse instance using the specified properties.
                     * @function create
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {de.fll.core.proto.ISessionResponse=} [properties] Properties to set
                     * @returns {de.fll.core.proto.SessionResponse} SessionResponse instance
                     */
                    SessionResponse.create = function create(properties) {
                        return new SessionResponse(properties);
                    };

                    /**
                     * Encodes the specified SessionResponse message. Does not implicitly {@link de.fll.core.proto.SessionResponse.verify|verify} messages.
                     * @function encode
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {de.fll.core.proto.ISessionResponse} message SessionResponse message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    SessionResponse.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.valid != null && Object.hasOwnProperty.call(message, "valid"))
                            writer.uint32(/* id 1, wireType 0 =*/8).bool(message.valid);
                        if (message.user != null && Object.hasOwnProperty.call(message, "user"))
                            $root.de.fll.core.proto.User.encode(message.user, writer.uint32(/* id 2, wireType 2 =*/18).fork()).ldelim();
                        if (message.error != null && Object.hasOwnProperty.call(message, "error"))
                            writer.uint32(/* id 3, wireType 2 =*/26).string(message.error);
                        return writer;
                    };

                    /**
                     * Encodes the specified SessionResponse message, length delimited. Does not implicitly {@link de.fll.core.proto.SessionResponse.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {de.fll.core.proto.ISessionResponse} message SessionResponse message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    SessionResponse.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a SessionResponse message from the specified reader or buffer.
                     * @function decode
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {de.fll.core.proto.SessionResponse} SessionResponse
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    SessionResponse.decode = function decode(reader, length, error) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.de.fll.core.proto.SessionResponse();
                        while (reader.pos < end) {
                            let tag = reader.uint32();
                            if (tag === error)
                                break;
                            switch (tag >>> 3) {
                            case 1: {
                                    message.valid = reader.bool();
                                    break;
                                }
                            case 2: {
                                    message.user = $root.de.fll.core.proto.User.decode(reader, reader.uint32());
                                    break;
                                }
                            case 3: {
                                    message.error = reader.string();
                                    break;
                                }
                            default:
                                reader.skipType(tag & 7);
                                break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a SessionResponse message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {de.fll.core.proto.SessionResponse} SessionResponse
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    SessionResponse.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a SessionResponse message.
                     * @function verify
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    SessionResponse.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.valid != null && message.hasOwnProperty("valid"))
                            if (typeof message.valid !== "boolean")
                                return "valid: boolean expected";
                        if (message.user != null && message.hasOwnProperty("user")) {
                            let error = $root.de.fll.core.proto.User.verify(message.user);
                            if (error)
                                return "user." + error;
                        }
                        if (message.error != null && message.hasOwnProperty("error"))
                            if (!$util.isString(message.error))
                                return "error: string expected";
                        return null;
                    };

                    /**
                     * Creates a SessionResponse message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {de.fll.core.proto.SessionResponse} SessionResponse
                     */
                    SessionResponse.fromObject = function fromObject(object) {
                        if (object instanceof $root.de.fll.core.proto.SessionResponse)
                            return object;
                        let message = new $root.de.fll.core.proto.SessionResponse();
                        if (object.valid != null)
                            message.valid = Boolean(object.valid);
                        if (object.user != null) {
                            if (typeof object.user !== "object")
                                throw TypeError(".de.fll.core.proto.SessionResponse.user: object expected");
                            message.user = $root.de.fll.core.proto.User.fromObject(object.user);
                        }
                        if (object.error != null)
                            message.error = String(object.error);
                        return message;
                    };

                    /**
                     * Creates a plain object from a SessionResponse message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {de.fll.core.proto.SessionResponse} message SessionResponse
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    SessionResponse.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        let object = {};
                        if (options.defaults) {
                            object.valid = false;
                            object.user = null;
                            object.error = "";
                        }
                        if (message.valid != null && message.hasOwnProperty("valid"))
                            object.valid = message.valid;
                        if (message.user != null && message.hasOwnProperty("user"))
                            object.user = $root.de.fll.core.proto.User.toObject(message.user, options);
                        if (message.error != null && message.hasOwnProperty("error"))
                            object.error = message.error;
                        return object;
                    };

                    /**
                     * Converts this SessionResponse to JSON.
                     * @function toJSON
                     * @memberof de.fll.core.proto.SessionResponse
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    SessionResponse.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    /**
                     * Gets the default type url for SessionResponse
                     * @function getTypeUrl
                     * @memberof de.fll.core.proto.SessionResponse
                     * @static
                     * @param {string} [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns {string} The default type url
                     */
                    SessionResponse.getTypeUrl = function getTypeUrl(typeUrlPrefix) {
                        if (typeUrlPrefix === undefined) {
                            typeUrlPrefix = "type.googleapis.com";
                        }
                        return typeUrlPrefix + "/de.fll.core.proto.SessionResponse";
                    };

                    return SessionResponse;
                })();

                proto.User = (function() {

                    /**
                     * Properties of a User.
                     * @memberof de.fll.core.proto
                     * @interface IUser
                     * @property {number|Long|null} [id] User id
                     * @property {string|null} [username] User username
                     * @property {string|null} [email] User email
                     */

                    /**
                     * Constructs a new User.
                     * @memberof de.fll.core.proto
                     * @classdesc Represents a User.
                     * @implements IUser
                     * @constructor
                     * @param {de.fll.core.proto.IUser=} [properties] Properties to set
                     */
                    function User(properties) {
                        if (properties)
                            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * User id.
                     * @member {number|Long} id
                     * @memberof de.fll.core.proto.User
                     * @instance
                     */
                    User.prototype.id = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

                    /**
                     * User username.
                     * @member {string} username
                     * @memberof de.fll.core.proto.User
                     * @instance
                     */
                    User.prototype.username = "";

                    /**
                     * User email.
                     * @member {string} email
                     * @memberof de.fll.core.proto.User
                     * @instance
                     */
                    User.prototype.email = "";

                    /**
                     * Creates a new User instance using the specified properties.
                     * @function create
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {de.fll.core.proto.IUser=} [properties] Properties to set
                     * @returns {de.fll.core.proto.User} User instance
                     */
                    User.create = function create(properties) {
                        return new User(properties);
                    };

                    /**
                     * Encodes the specified User message. Does not implicitly {@link de.fll.core.proto.User.verify|verify} messages.
                     * @function encode
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {de.fll.core.proto.IUser} message User message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    User.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int64(message.id);
                        if (message.username != null && Object.hasOwnProperty.call(message, "username"))
                            writer.uint32(/* id 2, wireType 2 =*/18).string(message.username);
                        if (message.email != null && Object.hasOwnProperty.call(message, "email"))
                            writer.uint32(/* id 3, wireType 2 =*/26).string(message.email);
                        return writer;
                    };

                    /**
                     * Encodes the specified User message, length delimited. Does not implicitly {@link de.fll.core.proto.User.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {de.fll.core.proto.IUser} message User message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    User.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a User message from the specified reader or buffer.
                     * @function decode
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {de.fll.core.proto.User} User
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    User.decode = function decode(reader, length, error) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.de.fll.core.proto.User();
                        while (reader.pos < end) {
                            let tag = reader.uint32();
                            if (tag === error)
                                break;
                            switch (tag >>> 3) {
                            case 1: {
                                    message.id = reader.int64();
                                    break;
                                }
                            case 2: {
                                    message.username = reader.string();
                                    break;
                                }
                            case 3: {
                                    message.email = reader.string();
                                    break;
                                }
                            default:
                                reader.skipType(tag & 7);
                                break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a User message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {de.fll.core.proto.User} User
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    User.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a User message.
                     * @function verify
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    User.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (!$util.isInteger(message.id) && !(message.id && $util.isInteger(message.id.low) && $util.isInteger(message.id.high)))
                                return "id: integer|Long expected";
                        if (message.username != null && message.hasOwnProperty("username"))
                            if (!$util.isString(message.username))
                                return "username: string expected";
                        if (message.email != null && message.hasOwnProperty("email"))
                            if (!$util.isString(message.email))
                                return "email: string expected";
                        return null;
                    };

                    /**
                     * Creates a User message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {de.fll.core.proto.User} User
                     */
                    User.fromObject = function fromObject(object) {
                        if (object instanceof $root.de.fll.core.proto.User)
                            return object;
                        let message = new $root.de.fll.core.proto.User();
                        if (object.id != null)
                            if ($util.Long)
                                (message.id = $util.Long.fromValue(object.id)).unsigned = false;
                            else if (typeof object.id === "string")
                                message.id = parseInt(object.id, 10);
                            else if (typeof object.id === "number")
                                message.id = object.id;
                            else if (typeof object.id === "object")
                                message.id = new $util.LongBits(object.id.low >>> 0, object.id.high >>> 0).toNumber();
                        if (object.username != null)
                            message.username = String(object.username);
                        if (object.email != null)
                            message.email = String(object.email);
                        return message;
                    };

                    /**
                     * Creates a plain object from a User message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {de.fll.core.proto.User} message User
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    User.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        let object = {};
                        if (options.defaults) {
                            if ($util.Long) {
                                let long = new $util.Long(0, 0, false);
                                object.id = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.id = options.longs === String ? "0" : 0;
                            object.username = "";
                            object.email = "";
                        }
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (typeof message.id === "number")
                                object.id = options.longs === String ? String(message.id) : message.id;
                            else
                                object.id = options.longs === String ? $util.Long.prototype.toString.call(message.id) : options.longs === Number ? new $util.LongBits(message.id.low >>> 0, message.id.high >>> 0).toNumber() : message.id;
                        if (message.username != null && message.hasOwnProperty("username"))
                            object.username = message.username;
                        if (message.email != null && message.hasOwnProperty("email"))
                            object.email = message.email;
                        return object;
                    };

                    /**
                     * Converts this User to JSON.
                     * @function toJSON
                     * @memberof de.fll.core.proto.User
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    User.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    /**
                     * Gets the default type url for User
                     * @function getTypeUrl
                     * @memberof de.fll.core.proto.User
                     * @static
                     * @param {string} [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns {string} The default type url
                     */
                    User.getTypeUrl = function getTypeUrl(typeUrlPrefix) {
                        if (typeUrlPrefix === undefined) {
                            typeUrlPrefix = "type.googleapis.com";
                        }
                        return typeUrlPrefix + "/de.fll.core.proto.User";
                    };

                    return User;
                })();

                proto.Score = (function() {

                    /**
                     * Properties of a Score.
                     * @memberof de.fll.core.proto
                     * @interface IScore
                     * @property {number|null} [points] Score points
                     * @property {number|null} [time] Score time
                     * @property {boolean|null} [highlight] Score highlight
                     */

                    /**
                     * Constructs a new Score.
                     * @memberof de.fll.core.proto
                     * @classdesc Represents a Score.
                     * @implements IScore
                     * @constructor
                     * @param {de.fll.core.proto.IScore=} [properties] Properties to set
                     */
                    function Score(properties) {
                        if (properties)
                            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * Score points.
                     * @member {number} points
                     * @memberof de.fll.core.proto.Score
                     * @instance
                     */
                    Score.prototype.points = 0;

                    /**
                     * Score time.
                     * @member {number} time
                     * @memberof de.fll.core.proto.Score
                     * @instance
                     */
                    Score.prototype.time = 0;

                    /**
                     * Score highlight.
                     * @member {boolean} highlight
                     * @memberof de.fll.core.proto.Score
                     * @instance
                     */
                    Score.prototype.highlight = false;

                    /**
                     * Creates a new Score instance using the specified properties.
                     * @function create
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {de.fll.core.proto.IScore=} [properties] Properties to set
                     * @returns {de.fll.core.proto.Score} Score instance
                     */
                    Score.create = function create(properties) {
                        return new Score(properties);
                    };

                    /**
                     * Encodes the specified Score message. Does not implicitly {@link de.fll.core.proto.Score.verify|verify} messages.
                     * @function encode
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {de.fll.core.proto.IScore} message Score message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    Score.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.points != null && Object.hasOwnProperty.call(message, "points"))
                            writer.uint32(/* id 1, wireType 1 =*/9).double(message.points);
                        if (message.time != null && Object.hasOwnProperty.call(message, "time"))
                            writer.uint32(/* id 2, wireType 0 =*/16).int32(message.time);
                        if (message.highlight != null && Object.hasOwnProperty.call(message, "highlight"))
                            writer.uint32(/* id 3, wireType 0 =*/24).bool(message.highlight);
                        return writer;
                    };

                    /**
                     * Encodes the specified Score message, length delimited. Does not implicitly {@link de.fll.core.proto.Score.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {de.fll.core.proto.IScore} message Score message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    Score.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a Score message from the specified reader or buffer.
                     * @function decode
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {de.fll.core.proto.Score} Score
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    Score.decode = function decode(reader, length, error) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.de.fll.core.proto.Score();
                        while (reader.pos < end) {
                            let tag = reader.uint32();
                            if (tag === error)
                                break;
                            switch (tag >>> 3) {
                            case 1: {
                                    message.points = reader.double();
                                    break;
                                }
                            case 2: {
                                    message.time = reader.int32();
                                    break;
                                }
                            case 3: {
                                    message.highlight = reader.bool();
                                    break;
                                }
                            default:
                                reader.skipType(tag & 7);
                                break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a Score message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {de.fll.core.proto.Score} Score
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    Score.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a Score message.
                     * @function verify
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    Score.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.points != null && message.hasOwnProperty("points"))
                            if (typeof message.points !== "number")
                                return "points: number expected";
                        if (message.time != null && message.hasOwnProperty("time"))
                            if (!$util.isInteger(message.time))
                                return "time: integer expected";
                        if (message.highlight != null && message.hasOwnProperty("highlight"))
                            if (typeof message.highlight !== "boolean")
                                return "highlight: boolean expected";
                        return null;
                    };

                    /**
                     * Creates a Score message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {de.fll.core.proto.Score} Score
                     */
                    Score.fromObject = function fromObject(object) {
                        if (object instanceof $root.de.fll.core.proto.Score)
                            return object;
                        let message = new $root.de.fll.core.proto.Score();
                        if (object.points != null)
                            message.points = Number(object.points);
                        if (object.time != null)
                            message.time = object.time | 0;
                        if (object.highlight != null)
                            message.highlight = Boolean(object.highlight);
                        return message;
                    };

                    /**
                     * Creates a plain object from a Score message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {de.fll.core.proto.Score} message Score
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    Score.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        let object = {};
                        if (options.defaults) {
                            object.points = 0;
                            object.time = 0;
                            object.highlight = false;
                        }
                        if (message.points != null && message.hasOwnProperty("points"))
                            object.points = options.json && !isFinite(message.points) ? String(message.points) : message.points;
                        if (message.time != null && message.hasOwnProperty("time"))
                            object.time = message.time;
                        if (message.highlight != null && message.hasOwnProperty("highlight"))
                            object.highlight = message.highlight;
                        return object;
                    };

                    /**
                     * Converts this Score to JSON.
                     * @function toJSON
                     * @memberof de.fll.core.proto.Score
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    Score.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    /**
                     * Gets the default type url for Score
                     * @function getTypeUrl
                     * @memberof de.fll.core.proto.Score
                     * @static
                     * @param {string} [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns {string} The default type url
                     */
                    Score.getTypeUrl = function getTypeUrl(typeUrlPrefix) {
                        if (typeUrlPrefix === undefined) {
                            typeUrlPrefix = "type.googleapis.com";
                        }
                        return typeUrlPrefix + "/de.fll.core.proto.Score";
                    };

                    return Score;
                })();

                proto.Team = (function() {

                    /**
                     * Properties of a Team.
                     * @memberof de.fll.core.proto
                     * @interface ITeam
                     * @property {number|Long|null} [id] Team id
                     * @property {string|null} [name] Team name
                     * @property {number|null} [rank] Team rank
                     * @property {Array.<de.fll.core.proto.IScore>|null} [scores] Team scores
                     */

                    /**
                     * Constructs a new Team.
                     * @memberof de.fll.core.proto
                     * @classdesc Represents a Team.
                     * @implements ITeam
                     * @constructor
                     * @param {de.fll.core.proto.ITeam=} [properties] Properties to set
                     */
                    function Team(properties) {
                        this.scores = [];
                        if (properties)
                            for (let keys = Object.keys(properties), i = 0; i < keys.length; ++i)
                                if (properties[keys[i]] != null)
                                    this[keys[i]] = properties[keys[i]];
                    }

                    /**
                     * Team id.
                     * @member {number|Long} id
                     * @memberof de.fll.core.proto.Team
                     * @instance
                     */
                    Team.prototype.id = $util.Long ? $util.Long.fromBits(0,0,false) : 0;

                    /**
                     * Team name.
                     * @member {string} name
                     * @memberof de.fll.core.proto.Team
                     * @instance
                     */
                    Team.prototype.name = "";

                    /**
                     * Team rank.
                     * @member {number} rank
                     * @memberof de.fll.core.proto.Team
                     * @instance
                     */
                    Team.prototype.rank = 0;

                    /**
                     * Team scores.
                     * @member {Array.<de.fll.core.proto.IScore>} scores
                     * @memberof de.fll.core.proto.Team
                     * @instance
                     */
                    Team.prototype.scores = $util.emptyArray;

                    /**
                     * Creates a new Team instance using the specified properties.
                     * @function create
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {de.fll.core.proto.ITeam=} [properties] Properties to set
                     * @returns {de.fll.core.proto.Team} Team instance
                     */
                    Team.create = function create(properties) {
                        return new Team(properties);
                    };

                    /**
                     * Encodes the specified Team message. Does not implicitly {@link de.fll.core.proto.Team.verify|verify} messages.
                     * @function encode
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {de.fll.core.proto.ITeam} message Team message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    Team.encode = function encode(message, writer) {
                        if (!writer)
                            writer = $Writer.create();
                        if (message.id != null && Object.hasOwnProperty.call(message, "id"))
                            writer.uint32(/* id 1, wireType 0 =*/8).int64(message.id);
                        if (message.name != null && Object.hasOwnProperty.call(message, "name"))
                            writer.uint32(/* id 2, wireType 2 =*/18).string(message.name);
                        if (message.rank != null && Object.hasOwnProperty.call(message, "rank"))
                            writer.uint32(/* id 3, wireType 0 =*/24).int32(message.rank);
                        if (message.scores != null && message.scores.length)
                            for (let i = 0; i < message.scores.length; ++i)
                                $root.de.fll.core.proto.Score.encode(message.scores[i], writer.uint32(/* id 4, wireType 2 =*/34).fork()).ldelim();
                        return writer;
                    };

                    /**
                     * Encodes the specified Team message, length delimited. Does not implicitly {@link de.fll.core.proto.Team.verify|verify} messages.
                     * @function encodeDelimited
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {de.fll.core.proto.ITeam} message Team message or plain object to encode
                     * @param {$protobuf.Writer} [writer] Writer to encode to
                     * @returns {$protobuf.Writer} Writer
                     */
                    Team.encodeDelimited = function encodeDelimited(message, writer) {
                        return this.encode(message, writer).ldelim();
                    };

                    /**
                     * Decodes a Team message from the specified reader or buffer.
                     * @function decode
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @param {number} [length] Message length if known beforehand
                     * @returns {de.fll.core.proto.Team} Team
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    Team.decode = function decode(reader, length, error) {
                        if (!(reader instanceof $Reader))
                            reader = $Reader.create(reader);
                        let end = length === undefined ? reader.len : reader.pos + length, message = new $root.de.fll.core.proto.Team();
                        while (reader.pos < end) {
                            let tag = reader.uint32();
                            if (tag === error)
                                break;
                            switch (tag >>> 3) {
                            case 1: {
                                    message.id = reader.int64();
                                    break;
                                }
                            case 2: {
                                    message.name = reader.string();
                                    break;
                                }
                            case 3: {
                                    message.rank = reader.int32();
                                    break;
                                }
                            case 4: {
                                    if (!(message.scores && message.scores.length))
                                        message.scores = [];
                                    message.scores.push($root.de.fll.core.proto.Score.decode(reader, reader.uint32()));
                                    break;
                                }
                            default:
                                reader.skipType(tag & 7);
                                break;
                            }
                        }
                        return message;
                    };

                    /**
                     * Decodes a Team message from the specified reader or buffer, length delimited.
                     * @function decodeDelimited
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {$protobuf.Reader|Uint8Array} reader Reader or buffer to decode from
                     * @returns {de.fll.core.proto.Team} Team
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    Team.decodeDelimited = function decodeDelimited(reader) {
                        if (!(reader instanceof $Reader))
                            reader = new $Reader(reader);
                        return this.decode(reader, reader.uint32());
                    };

                    /**
                     * Verifies a Team message.
                     * @function verify
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {Object.<string,*>} message Plain object to verify
                     * @returns {string|null} `null` if valid, otherwise the reason why it is not
                     */
                    Team.verify = function verify(message) {
                        if (typeof message !== "object" || message === null)
                            return "object expected";
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (!$util.isInteger(message.id) && !(message.id && $util.isInteger(message.id.low) && $util.isInteger(message.id.high)))
                                return "id: integer|Long expected";
                        if (message.name != null && message.hasOwnProperty("name"))
                            if (!$util.isString(message.name))
                                return "name: string expected";
                        if (message.rank != null && message.hasOwnProperty("rank"))
                            if (!$util.isInteger(message.rank))
                                return "rank: integer expected";
                        if (message.scores != null && message.hasOwnProperty("scores")) {
                            if (!Array.isArray(message.scores))
                                return "scores: array expected";
                            for (let i = 0; i < message.scores.length; ++i) {
                                let error = $root.de.fll.core.proto.Score.verify(message.scores[i]);
                                if (error)
                                    return "scores." + error;
                            }
                        }
                        return null;
                    };

                    /**
                     * Creates a Team message from a plain object. Also converts values to their respective internal types.
                     * @function fromObject
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {Object.<string,*>} object Plain object
                     * @returns {de.fll.core.proto.Team} Team
                     */
                    Team.fromObject = function fromObject(object) {
                        if (object instanceof $root.de.fll.core.proto.Team)
                            return object;
                        let message = new $root.de.fll.core.proto.Team();
                        if (object.id != null)
                            if ($util.Long)
                                (message.id = $util.Long.fromValue(object.id)).unsigned = false;
                            else if (typeof object.id === "string")
                                message.id = parseInt(object.id, 10);
                            else if (typeof object.id === "number")
                                message.id = object.id;
                            else if (typeof object.id === "object")
                                message.id = new $util.LongBits(object.id.low >>> 0, object.id.high >>> 0).toNumber();
                        if (object.name != null)
                            message.name = String(object.name);
                        if (object.rank != null)
                            message.rank = object.rank | 0;
                        if (object.scores) {
                            if (!Array.isArray(object.scores))
                                throw TypeError(".de.fll.core.proto.Team.scores: array expected");
                            message.scores = [];
                            for (let i = 0; i < object.scores.length; ++i) {
                                if (typeof object.scores[i] !== "object")
                                    throw TypeError(".de.fll.core.proto.Team.scores: object expected");
                                message.scores[i] = $root.de.fll.core.proto.Score.fromObject(object.scores[i]);
                            }
                        }
                        return message;
                    };

                    /**
                     * Creates a plain object from a Team message. Also converts values to other types if specified.
                     * @function toObject
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {de.fll.core.proto.Team} message Team
                     * @param {$protobuf.IConversionOptions} [options] Conversion options
                     * @returns {Object.<string,*>} Plain object
                     */
                    Team.toObject = function toObject(message, options) {
                        if (!options)
                            options = {};
                        let object = {};
                        if (options.arrays || options.defaults)
                            object.scores = [];
                        if (options.defaults) {
                            if ($util.Long) {
                                let long = new $util.Long(0, 0, false);
                                object.id = options.longs === String ? long.toString() : options.longs === Number ? long.toNumber() : long;
                            } else
                                object.id = options.longs === String ? "0" : 0;
                            object.name = "";
                            object.rank = 0;
                        }
                        if (message.id != null && message.hasOwnProperty("id"))
                            if (typeof message.id === "number")
                                object.id = options.longs === String ? String(message.id) : message.id;
                            else
                                object.id = options.longs === String ? $util.Long.prototype.toString.call(message.id) : options.longs === Number ? new $util.LongBits(message.id.low >>> 0, message.id.high >>> 0).toNumber() : message.id;
                        if (message.name != null && message.hasOwnProperty("name"))
                            object.name = message.name;
                        if (message.rank != null && message.hasOwnProperty("rank"))
                            object.rank = message.rank;
                        if (message.scores && message.scores.length) {
                            object.scores = [];
                            for (let j = 0; j < message.scores.length; ++j)
                                object.scores[j] = $root.de.fll.core.proto.Score.toObject(message.scores[j], options);
                        }
                        return object;
                    };

                    /**
                     * Converts this Team to JSON.
                     * @function toJSON
                     * @memberof de.fll.core.proto.Team
                     * @instance
                     * @returns {Object.<string,*>} JSON object
                     */
                    Team.prototype.toJSON = function toJSON() {
                        return this.constructor.toObject(this, $protobuf.util.toJSONOptions);
                    };

                    /**
                     * Gets the default type url for Team
                     * @function getTypeUrl
                     * @memberof de.fll.core.proto.Team
                     * @static
                     * @param {string} [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns {string} The default type url
                     */
                    Team.getTypeUrl = function getTypeUrl(typeUrlPrefix) {
                        if (typeUrlPrefix === undefined) {
                            typeUrlPrefix = "type.googleapis.com";
                        }
                        return typeUrlPrefix + "/de.fll.core.proto.Team";
                    };

                    return Team;
                })();

                return proto;
            })();

            return core;
        })();

        return fll;
    })();

    return de;
})();

export { $root as default };

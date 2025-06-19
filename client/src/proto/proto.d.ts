import * as $protobuf from "protobufjs";
import Long = require("long");
/** Namespace de. */
export namespace de {

    /** Namespace fll. */
    namespace fll {

        /** Namespace core. */
        namespace core {

            /** Namespace proto. */
            namespace proto {

                /** Properties of a LoginRequest. */
                interface ILoginRequest {

                    /** LoginRequest username */
                    username?: (string|null);

                    /** LoginRequest password */
                    password?: (string|null);
                }

                /** Represents a LoginRequest. */
                class LoginRequest implements ILoginRequest {

                    /**
                     * Constructs a new LoginRequest.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: de.fll.core.proto.ILoginRequest);

                    /** LoginRequest username. */
                    public username: string;

                    /** LoginRequest password. */
                    public password: string;

                    /**
                     * Creates a new LoginRequest instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns LoginRequest instance
                     */
                    public static create(properties?: de.fll.core.proto.ILoginRequest): de.fll.core.proto.LoginRequest;

                    /**
                     * Encodes the specified LoginRequest message. Does not implicitly {@link de.fll.core.proto.LoginRequest.verify|verify} messages.
                     * @param message LoginRequest message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: de.fll.core.proto.ILoginRequest, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified LoginRequest message, length delimited. Does not implicitly {@link de.fll.core.proto.LoginRequest.verify|verify} messages.
                     * @param message LoginRequest message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: de.fll.core.proto.ILoginRequest, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a LoginRequest message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns LoginRequest
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): de.fll.core.proto.LoginRequest;

                    /**
                     * Decodes a LoginRequest message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns LoginRequest
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): de.fll.core.proto.LoginRequest;

                    /**
                     * Verifies a LoginRequest message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a LoginRequest message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns LoginRequest
                     */
                    public static fromObject(object: { [k: string]: any }): de.fll.core.proto.LoginRequest;

                    /**
                     * Creates a plain object from a LoginRequest message. Also converts values to other types if specified.
                     * @param message LoginRequest
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: de.fll.core.proto.LoginRequest, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this LoginRequest to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };

                    /**
                     * Gets the default type url for LoginRequest
                     * @param [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns The default type url
                     */
                    public static getTypeUrl(typeUrlPrefix?: string): string;
                }

                /** Properties of a LoginResponse. */
                interface ILoginResponse {

                    /** LoginResponse success */
                    success?: (boolean|null);

                    /** LoginResponse token */
                    token?: (string|null);

                    /** LoginResponse error */
                    error?: (string|null);

                    /** LoginResponse user */
                    user?: (de.fll.core.proto.IUser|null);
                }

                /** Represents a LoginResponse. */
                class LoginResponse implements ILoginResponse {

                    /**
                     * Constructs a new LoginResponse.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: de.fll.core.proto.ILoginResponse);

                    /** LoginResponse success. */
                    public success: boolean;

                    /** LoginResponse token. */
                    public token: string;

                    /** LoginResponse error. */
                    public error: string;

                    /** LoginResponse user. */
                    public user?: (de.fll.core.proto.IUser|null);

                    /**
                     * Creates a new LoginResponse instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns LoginResponse instance
                     */
                    public static create(properties?: de.fll.core.proto.ILoginResponse): de.fll.core.proto.LoginResponse;

                    /**
                     * Encodes the specified LoginResponse message. Does not implicitly {@link de.fll.core.proto.LoginResponse.verify|verify} messages.
                     * @param message LoginResponse message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: de.fll.core.proto.ILoginResponse, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified LoginResponse message, length delimited. Does not implicitly {@link de.fll.core.proto.LoginResponse.verify|verify} messages.
                     * @param message LoginResponse message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: de.fll.core.proto.ILoginResponse, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a LoginResponse message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns LoginResponse
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): de.fll.core.proto.LoginResponse;

                    /**
                     * Decodes a LoginResponse message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns LoginResponse
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): de.fll.core.proto.LoginResponse;

                    /**
                     * Verifies a LoginResponse message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a LoginResponse message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns LoginResponse
                     */
                    public static fromObject(object: { [k: string]: any }): de.fll.core.proto.LoginResponse;

                    /**
                     * Creates a plain object from a LoginResponse message. Also converts values to other types if specified.
                     * @param message LoginResponse
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: de.fll.core.proto.LoginResponse, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this LoginResponse to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };

                    /**
                     * Gets the default type url for LoginResponse
                     * @param [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns The default type url
                     */
                    public static getTypeUrl(typeUrlPrefix?: string): string;
                }

                /** Properties of a SessionResponse. */
                interface ISessionResponse {

                    /** SessionResponse valid */
                    valid?: (boolean|null);

                    /** SessionResponse user */
                    user?: (de.fll.core.proto.IUser|null);

                    /** SessionResponse error */
                    error?: (string|null);
                }

                /** Represents a SessionResponse. */
                class SessionResponse implements ISessionResponse {

                    /**
                     * Constructs a new SessionResponse.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: de.fll.core.proto.ISessionResponse);

                    /** SessionResponse valid. */
                    public valid: boolean;

                    /** SessionResponse user. */
                    public user?: (de.fll.core.proto.IUser|null);

                    /** SessionResponse error. */
                    public error: string;

                    /**
                     * Creates a new SessionResponse instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns SessionResponse instance
                     */
                    public static create(properties?: de.fll.core.proto.ISessionResponse): de.fll.core.proto.SessionResponse;

                    /**
                     * Encodes the specified SessionResponse message. Does not implicitly {@link de.fll.core.proto.SessionResponse.verify|verify} messages.
                     * @param message SessionResponse message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: de.fll.core.proto.ISessionResponse, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified SessionResponse message, length delimited. Does not implicitly {@link de.fll.core.proto.SessionResponse.verify|verify} messages.
                     * @param message SessionResponse message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: de.fll.core.proto.ISessionResponse, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a SessionResponse message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns SessionResponse
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): de.fll.core.proto.SessionResponse;

                    /**
                     * Decodes a SessionResponse message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns SessionResponse
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): de.fll.core.proto.SessionResponse;

                    /**
                     * Verifies a SessionResponse message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a SessionResponse message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns SessionResponse
                     */
                    public static fromObject(object: { [k: string]: any }): de.fll.core.proto.SessionResponse;

                    /**
                     * Creates a plain object from a SessionResponse message. Also converts values to other types if specified.
                     * @param message SessionResponse
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: de.fll.core.proto.SessionResponse, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this SessionResponse to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };

                    /**
                     * Gets the default type url for SessionResponse
                     * @param [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns The default type url
                     */
                    public static getTypeUrl(typeUrlPrefix?: string): string;
                }

                /** Properties of a User. */
                interface IUser {

                    /** User id */
                    id?: (number|Long|null);

                    /** User username */
                    username?: (string|null);

                    /** User email */
                    email?: (string|null);
                }

                /** Represents a User. */
                class User implements IUser {

                    /**
                     * Constructs a new User.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: de.fll.core.proto.IUser);

                    /** User id. */
                    public id: (number|Long);

                    /** User username. */
                    public username: string;

                    /** User email. */
                    public email: string;

                    /**
                     * Creates a new User instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns User instance
                     */
                    public static create(properties?: de.fll.core.proto.IUser): de.fll.core.proto.User;

                    /**
                     * Encodes the specified User message. Does not implicitly {@link de.fll.core.proto.User.verify|verify} messages.
                     * @param message User message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: de.fll.core.proto.IUser, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified User message, length delimited. Does not implicitly {@link de.fll.core.proto.User.verify|verify} messages.
                     * @param message User message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: de.fll.core.proto.IUser, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a User message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns User
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): de.fll.core.proto.User;

                    /**
                     * Decodes a User message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns User
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): de.fll.core.proto.User;

                    /**
                     * Verifies a User message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a User message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns User
                     */
                    public static fromObject(object: { [k: string]: any }): de.fll.core.proto.User;

                    /**
                     * Creates a plain object from a User message. Also converts values to other types if specified.
                     * @param message User
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: de.fll.core.proto.User, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this User to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };

                    /**
                     * Gets the default type url for User
                     * @param [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns The default type url
                     */
                    public static getTypeUrl(typeUrlPrefix?: string): string;
                }

                /** Properties of a Score. */
                interface IScore {

                    /** Score points */
                    points?: (number|null);

                    /** Score time */
                    time?: (number|null);

                    /** Score highlight */
                    highlight?: (boolean|null);
                }

                /** Represents a Score. */
                class Score implements IScore {

                    /**
                     * Constructs a new Score.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: de.fll.core.proto.IScore);

                    /** Score points. */
                    public points: number;

                    /** Score time. */
                    public time: number;

                    /** Score highlight. */
                    public highlight: boolean;

                    /**
                     * Creates a new Score instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns Score instance
                     */
                    public static create(properties?: de.fll.core.proto.IScore): de.fll.core.proto.Score;

                    /**
                     * Encodes the specified Score message. Does not implicitly {@link de.fll.core.proto.Score.verify|verify} messages.
                     * @param message Score message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: de.fll.core.proto.IScore, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified Score message, length delimited. Does not implicitly {@link de.fll.core.proto.Score.verify|verify} messages.
                     * @param message Score message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: de.fll.core.proto.IScore, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a Score message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns Score
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): de.fll.core.proto.Score;

                    /**
                     * Decodes a Score message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns Score
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): de.fll.core.proto.Score;

                    /**
                     * Verifies a Score message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a Score message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns Score
                     */
                    public static fromObject(object: { [k: string]: any }): de.fll.core.proto.Score;

                    /**
                     * Creates a plain object from a Score message. Also converts values to other types if specified.
                     * @param message Score
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: de.fll.core.proto.Score, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this Score to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };

                    /**
                     * Gets the default type url for Score
                     * @param [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns The default type url
                     */
                    public static getTypeUrl(typeUrlPrefix?: string): string;
                }

                /** Properties of a Team. */
                interface ITeam {

                    /** Team id */
                    id?: (number|Long|null);

                    /** Team name */
                    name?: (string|null);

                    /** Team rank */
                    rank?: (number|null);

                    /** Team scores */
                    scores?: (de.fll.core.proto.IScore[]|null);
                }

                /** Represents a Team. */
                class Team implements ITeam {

                    /**
                     * Constructs a new Team.
                     * @param [properties] Properties to set
                     */
                    constructor(properties?: de.fll.core.proto.ITeam);

                    /** Team id. */
                    public id: (number|Long);

                    /** Team name. */
                    public name: string;

                    /** Team rank. */
                    public rank: number;

                    /** Team scores. */
                    public scores: de.fll.core.proto.IScore[];

                    /**
                     * Creates a new Team instance using the specified properties.
                     * @param [properties] Properties to set
                     * @returns Team instance
                     */
                    public static create(properties?: de.fll.core.proto.ITeam): de.fll.core.proto.Team;

                    /**
                     * Encodes the specified Team message. Does not implicitly {@link de.fll.core.proto.Team.verify|verify} messages.
                     * @param message Team message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encode(message: de.fll.core.proto.ITeam, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Encodes the specified Team message, length delimited. Does not implicitly {@link de.fll.core.proto.Team.verify|verify} messages.
                     * @param message Team message or plain object to encode
                     * @param [writer] Writer to encode to
                     * @returns Writer
                     */
                    public static encodeDelimited(message: de.fll.core.proto.ITeam, writer?: $protobuf.Writer): $protobuf.Writer;

                    /**
                     * Decodes a Team message from the specified reader or buffer.
                     * @param reader Reader or buffer to decode from
                     * @param [length] Message length if known beforehand
                     * @returns Team
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decode(reader: ($protobuf.Reader|Uint8Array), length?: number): de.fll.core.proto.Team;

                    /**
                     * Decodes a Team message from the specified reader or buffer, length delimited.
                     * @param reader Reader or buffer to decode from
                     * @returns Team
                     * @throws {Error} If the payload is not a reader or valid buffer
                     * @throws {$protobuf.util.ProtocolError} If required fields are missing
                     */
                    public static decodeDelimited(reader: ($protobuf.Reader|Uint8Array)): de.fll.core.proto.Team;

                    /**
                     * Verifies a Team message.
                     * @param message Plain object to verify
                     * @returns `null` if valid, otherwise the reason why it is not
                     */
                    public static verify(message: { [k: string]: any }): (string|null);

                    /**
                     * Creates a Team message from a plain object. Also converts values to their respective internal types.
                     * @param object Plain object
                     * @returns Team
                     */
                    public static fromObject(object: { [k: string]: any }): de.fll.core.proto.Team;

                    /**
                     * Creates a plain object from a Team message. Also converts values to other types if specified.
                     * @param message Team
                     * @param [options] Conversion options
                     * @returns Plain object
                     */
                    public static toObject(message: de.fll.core.proto.Team, options?: $protobuf.IConversionOptions): { [k: string]: any };

                    /**
                     * Converts this Team to JSON.
                     * @returns JSON object
                     */
                    public toJSON(): { [k: string]: any };

                    /**
                     * Gets the default type url for Team
                     * @param [typeUrlPrefix] your custom typeUrlPrefix(default "type.googleapis.com")
                     * @returns The default type url
                     */
                    public static getTypeUrl(typeUrlPrefix?: string): string;
                }
            }
        }
    }
}

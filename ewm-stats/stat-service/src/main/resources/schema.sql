CREATE TABLE IF NOT EXISTS endpoint_hits
(
    hit_id
    bigint
    NOT
    NULL
    GENERATED
    BY
    DEFAULT AS
    IDENTITY,
    app
    varchar
(
    256
) NOT NULL,
    uri varchar
(
    256
) NOT NULL,
    ip varchar
(
    32
) NOT NULL,
    hit_timestamp timestamp with time zone NOT NULL,
                                CONSTRAINT hit_pkey PRIMARY KEY (hit_id)
    );
-- AI pgvector schema. Keep the vector dimension aligned with com.disk.ai.pgvector.dimension.
create extension if not exists vector;

create table if not exists ai_document_index (
    id bigserial primary key,
    user_id bigint not null,
    user_file_id bigint not null,
    real_file_id bigint not null,
    filename varchar(512) not null,
    file_suffix varchar(64),
    media_type varchar(128),
    parser varchar(128),
    content_length bigint not null default 0,
    plain_text_chars integer not null default 0,
    block_count integer not null default 0,
    chunk_count integer not null default 0,
    gmt_create timestamp not null default current_timestamp,
    gmt_modified timestamp not null default current_timestamp,
    unique (user_id, user_file_id)
);

create table if not exists ai_document_chunk_vector (
    id bigserial primary key,
    user_id bigint not null,
    user_file_id bigint not null,
    real_file_id bigint not null,
    filename varchar(512) not null,
    file_suffix varchar(64),
    media_type varchar(128),
    parser varchar(128),
    block_index integer not null,
    chunk_index integer not null,
    start_offset integer not null default 0,
    end_offset integer not null default 0,
    token_estimate integer not null default 0,
    chunk_text text not null,
    embedding vector(768) not null,
    gmt_create timestamp not null default current_timestamp
);

create index if not exists idx_ai_document_chunk_owner
    on ai_document_chunk_vector(user_id, user_file_id);

create table if not exists ai_document_result (
    id bigserial primary key,
    user_id bigint not null,
    user_file_id bigint not null,
    filename varchar(512) not null,
    summary_text text,
    summary_model varchar(128),
    summary_mocked boolean,
    tags_json jsonb,
    tags_model varchar(128),
    tags_mocked boolean,
    gmt_create timestamp not null default current_timestamp,
    gmt_modified timestamp not null default current_timestamp,
    unique (user_id, user_file_id)
);

create index if not exists idx_ai_document_result_owner
    on ai_document_result(user_id, user_file_id);

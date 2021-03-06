#!/bin/bash
set -e
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE SEQUENCE public.INVOICE_SEQUENCE
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE public.INVOICE_ITEM_SEQUENCE
   START WITH 1
   INCREMENT BY 1;


CREATE TABLE public.invoice (
    id bigint NOT NULL,
    account_id character varying(255),
    original_file_name character varying(255),
    paid_on timestamp without time zone,
    referential_number character varying(255),
    currency character varying(4),
    total_paid double precision,
    transaction_id character varying(255)
);

CREATE TABLE public.invoice_item (
    id bigint NOT NULL,
    campaign_name character varying(1024),
    prefix character varying(16),
    price double precision,
    invoice_id bigint
);

ALTER TABLE ONLY public.invoice_item
    ADD CONSTRAINT invoice_item_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.invoice
    ADD CONSTRAINT invoice_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.invoice_item
    ADD CONSTRAINT fkbu6tmpd0mtgu9wrw5bj5uv09v FOREIGN KEY (invoice_id) REFERENCES public.invoice(id);

EOSQL
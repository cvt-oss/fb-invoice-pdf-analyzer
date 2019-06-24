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

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

SET default_tablespace = '';

SET default_with_oids = false;

CREATE TABLE public.invoice (
    id bigint NOT NULL,
    accountid character varying(255),
    currency character varying(255),
    originalfilename character varying(255),
    paidon timestamp without time zone,
    referentialnumber character varying(255),
    totalpaid double precision,
    transactionid character varying(255)
);

CREATE TABLE public.invoiceitem (
    id bigint NOT NULL,
    campaignname character varying(255),
    prefix character varying(255),
    price double precision,
    invoice_id bigint
);


ALTER TABLE ONLY public.invoice
    ADD CONSTRAINT invoice_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.invoiceitem
    ADD CONSTRAINT invoiceitem_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.invoiceitem
    ADD CONSTRAINT fkm1qstfhfwpv8oip90w8fdxfl3 FOREIGN KEY (invoice_id) REFERENCES public.invoice(id);

EOSQL
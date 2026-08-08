CREATE TYPE MediaType AS ENUM (
    'IMAGE',
    'VIDEO'
    );

CREATE TYPE SaleType AS ENUM (
    'BY_QUANTITY',
    'UNTIL_SOLD_OUT',
    'MADE_TO_ORDER'
    );

CREATE TABLE IF NOT EXISTS universities
(
    id          UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(160) NOT NULL UNIQUE,
    acronym     VARCHAR(10)  NOT NULL UNIQUE,
    -- Color caracteristico de la universidad, ejemplo: La UTSV su color es el verde
    brand_color VARCHAR(15) CHECK (brand_color ~* '^#([a-f0-9]{6}|[a-f0-9]{3})$'),
    logo_url    TEXT         NOT NULL,
    state       VARCHAR(50)  NOT NULL,
    city        VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS university_domains
(
    id            UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    university_id UUID        NOT NULL REFERENCES universities (id) ON DELETE CASCADE,
    domain        VARCHAR(75) NOT NULL UNIQUE CHECK (domain ~* '^([a-z0-9]+(-[a-z0-9]+)*\.)+[a-z]{2,}$'),
    CONSTRAINT university_domains_unique_0 UNIQUE (university_id, domain) -- Asegura que no se repitan dominios para la misma universidad
);

CREATE TABLE IF NOT EXISTS maps
(
    -- ID de universidad
    university_id  UUID           NOT NULL PRIMARY KEY REFERENCES universities (id)
        ON UPDATE NO ACTION ON DELETE CASCADE,
    -- Dato para ubicacion en el mapa
    latitude       NUMERIC(10, 8) NOT NULL,
    -- Dato para ubicacion en el mapa
    longitude      NUMERIC(10, 8) NOT NULL,
    -- guardar URL de imagen de mapa personalizado para cargarlo en la UI
    custom_map_url TEXT           NOT NULL
);

CREATE TABLE IF NOT EXISTS buildings
(
    id        UUID           NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name      VARCHAR(60)    NOT NULL,
    -- longitud de la ubicacion del edificio
    longitude NUMERIC(10, 8) NOT NULL,
    -- latitud de la ubicacion del edificio
    latitude  NUMERIC(10, 8) NOT NULL,
    map_id    UUID           NOT NULL REFERENCES maps (university_id)
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS users
(
    id            UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    email         VARCHAR(120) NOT NULL UNIQUE,
    password      VARCHAR(300) NOT NULL,
    verified      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    university_id UUID         NOT NULL REFERENCES universities (id)
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS roles
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS permissions
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS role_permissions
(
    role_id       SERIAL NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_id SERIAL NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS user_roles
(
    user_id UUID   NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id SERIAL NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id         UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token      UUID        NOT NULL UNIQUE,
    revoked    BOOLEAN     NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS verification_tokens
(
    user_id    UUID        NOT NULL PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    token      UUID        NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS salespeople
(
    user_id           UUID NOT NULL PRIMARY KEY REFERENCES users (id)
        ON UPDATE NO ACTION ON DELETE CASCADE,
    has_card_terminal BOOLEAN
);

CREATE TABLE IF NOT EXISTS publications
(
    id             UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title          VARCHAR(80)  NOT NULL,
    description    VARCHAR(255) NOT NULL,
    -- fecha en que es posteada o reposteada
    posted_at      TIMESTAMPTZ,
    -- fecha de creacion de la publicacion
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    expires_at     TIMESTAMPTZ,
    salesperson_id UUID         NOT NULL REFERENCES salespeople (user_id) ON DELETE CASCADE,
    -- ID del edificio donde se encuentra el vendedor con el producto, puede actualizarse si el vendedor se mueve a otro edificio, para que los compradores puedan ver la ubicación actual del vendedor
    building_id    UUID REFERENCES buildings (id) ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS publication_media
(
    id             BIGSERIAL PRIMARY KEY,
    media_url      TEXT      NOT NULL,
    -- Dado que las URLs son siempre diferentes usar UUID para mantener la unicidad estaria de más para datos tan simples en este caso
    media_type     MEDIATYPE NOT NULL,
    display_order  INTEGER   NOT NULL DEFAULT 0,
    publication_id UUID      NOT NULL REFERENCES publications (id)
        ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS products
(
    publication_id UUID           NOT NULL PRIMARY KEY REFERENCES publications (id)
        ON UPDATE NO ACTION ON DELETE CASCADE,
    sale_price     NUMERIC(10, 2) NOT NULL,
    inventory      NUMERIC(10, 2) NOT NULL DEFAULT 0,
    type_sale      SALETYPE       NOT NULL,
    allows_layaway BOOLEAN        NOT NULL DEFAULT FALSE,
    is_active      BOOLEAN        NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS profiles
(
    user_id     UUID        NOT NULL PRIMARY KEY REFERENCES users (id)
        ON UPDATE NO ACTION ON DELETE CASCADE,
    first_name  VARCHAR(60) NOT NULL,
    last_name   VARCHAR(60) NOT NULL,
    photo_url   TEXT,
    -- Edificio al que pertenece
    building_id UUID REFERENCES buildings (id)
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS bank_cards
(
    user_id           UUID        NOT NULL PRIMARY KEY REFERENCES salespeople (user_id)
        ON UPDATE NO ACTION ON DELETE CASCADE,
    account_number    VARCHAR(18) NOT NULL,
    recipient         VARCHAR(70) NOT NULL,
    payment_reference VARCHAR(60) NOT NULL
);

CREATE TYPE TagType AS ENUM (
    'CATEGORY', -- Para: Comida, Electrónica, Ropa, Libros
    'SALE_MODIFIER', -- Para: Por pedido, Acepta apartados, Entrega inmediata
    'PAYMENT_METHOD' -- Para: Solo efectivo, Transferencia, Terminal
    );

CREATE TABLE IF NOT EXISTS tags
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    type TagType     NOT NULL
);

CREATE TABLE IF NOT EXISTS tag_publication
(
    tag_id         SERIAL NOT NULL REFERENCES tags (id) ON DELETE CASCADE,
    publication_id UUID   NOT NULL REFERENCES publications (id) ON DELETE CASCADE,
    PRIMARY KEY (tag_id, publication_id)
);

CREATE TABLE IF NOT EXISTS publication_reviews
(
    id             UUID     NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    publication_id UUID     NOT NULL REFERENCES publications (id)
        ON UPDATE NO ACTION ON DELETE CASCADE,
    user_id        UUID     NOT NULL REFERENCES users (id)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    rating         SMALLINT NOT NULL CHECK (rating >= 1 AND rating <= 10),
    CONSTRAINT publication_reviews_unique_0 UNIQUE (publication_id, user_id)
);

CREATE TABLE IF NOT EXISTS publication_comment
(
    id             UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    publication_id UUID        NOT NULL REFERENCES publications (id)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    user_id        UUID        NOT NULL REFERENCES users (id)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    content        TEXT        NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS favorite_publications
(
    publication_id UUID NOT NULL REFERENCES publications (id) ON DELETE CASCADE,
    user_id        UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (publication_id, user_id),
    CONSTRAINT favorite_publications_unique_0 UNIQUE (publication_id, user_id)
);


CREATE TABLE IF NOT EXISTS private_messages
(
    id           UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    content      VARCHAR(255) NOT NULL,
    -- ID de usuario
    sender_id    UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    -- Id de usuario
    recipient_id UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    sent_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT check_no_self_messages CHECK (sender_id <> recipient_id)
);

CREATE INDEX idx_pm_recipient_sent ON private_messages (recipient_id, sent_at DESC);
CREATE INDEX idx_pm_sender_sent ON private_messages (sender_id, sent_at DESC);

CREATE TABLE IF NOT EXISTS layaway_product
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    UUID NOT NULL REFERENCES users (id)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    product_id UUID NOT NULL REFERENCES products (publication_id)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    quantity   DECIMAL(10, 2) NOT NULL DEFAULT 0
);

CREATE TYPE NotificationType AS ENUM (
    'NEW_MESSAGE',
    'NEW_RESERVATION',
    'NEW_COMMENT',
    'NEW_REVIEW',
    'NEW_LIKE',
    'BUYER_ON_THE_WAY',
    'SELLER_ON_THE_WAY',
    'RESERVATION_CANCELLED'
    );

CREATE TABLE IF NOT EXISTS notifications
(
    id           UUID PRIMARY KEY          DEFAULT gen_random_uuid(),
    user_id      UUID             NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    type         NotificationType NOT NULL,
    reference_id UUID, -- id del mensaje, reserva, publicación, etc.
    description  VARCHAR(255)     NOT NULL,
    is_read      BOOLEAN          NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE INDEX idx_notifications_user_unread ON notifications (user_id, is_read, created_at DESC);

CREATE TABLE IF NOT EXISTS reports_publication
(
    id             UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    publication_id UUID         NOT NULL REFERENCES publications (id)
        ON UPDATE NO ACTION ON DELETE CASCADE,
    user_id        UUID         NOT NULL REFERENCES users (id)
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    reason         VARCHAR(255) NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT reports_publication_unique_0 UNIQUE (publication_id, user_id)
);

CREATE INDEX idx_reports_publication_user ON reports_publication (publication_id, user_id);

-- Diseño de tabla para el chat de la universidad de forma simple para el MVP, se puede mejorar en el futuro con más funcionalidades como hilos de conversación, reacciones, etc.
CREATE TABLE campus_chat_messages
(
    id            UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    university_id UUID        NOT NULL REFERENCES universities (id) ON DELETE CASCADE,
    user_id       UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    content       TEXT        NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
--
-- YouTube client model (for caching results)
--
-- The tables can be grouped as follows:
--
-- - Primary - anything with an 'etag' value. This directly refers to an API call.
--
-- - Secondary - anything directly related to a primary table via a one-to-one or
--   one-to-many relationship. (Not many-to-many - handled separately.
--

-- ------------------------------------------------------------
-- Primary tables
-- ------------------------------------------------------------

--
-- Channel
--
create table channel
(
    id            text not null,
    etag          text not null,
    custom_url    text not null, -- youtube user, e.g., @mrballen
    title         text,
    description   text,
    content_owner text,          -- always null ?
    uploads       text not null, -- always 'playlist_id' ?
    published_at  timestamp(0) with time zone,
    tn_url        text,          -- https://yt3.ggpht.com/...
    country       text,
    lang          text,
    privacy       text,

    constraint channel_pkey primary key (id)
);

--
-- Playlist
--
create table playlist
(
    id            text not null,
    etag          text not null,
    channel_id    text not null,
    channel_title text,
    published_at  timestamp(0) with time zone,
    tn_url        text, -- https://i.ytimg.com/vi/...
    lang          text,
    embed_html    text,
    -- thumbnail_video_id text = always null?

    constraint playlist_pkey primary key (id)
);

--
-- Individual item on Youtube Playlist
--
create table playlist_item
(
    id                 text not null,
    etag               text not null,
    playlist_id        text not null,
    channel_id         text not null, -- redundant since playlist has channel_id ?
    video_id           text,
    title              text,
    description        text,          -- may be null
    note               text,          -- always null ?
    tn_url             text,          -- https://i.ytimg.com/vi/...
    owner_channel_id   text,          -- ever different from channel_id?
    published_at       timestamp(0) with time zone,
    video_published_at timestamp(0) with time zone,
    position           int4,
    kind               text,          -- always 'youtube#video' ?

    constraint playlist_item_pkey primary key (id)
    -- constraint playlist_item_playlist_fkey foreign key (playlist_id) references playlist(id)
    -- constraint playlist_item_channel_fkey foreign key (channel_id) references channel(id)
);

--
-- Youtube Video
--
create table video
(
    id                  text not null,
    etag                text not null,
    channel_id          text not null,
    category_id         int4,
    title               text,
    description         text,
    lang                text,
    published_at        timestamp(0) with time zone,
    channel_title       text,
    embed_html          text,
    -- embed_height  int4 = 270
    -- embed_width   int4 = 480
    embeddable          boolean,
    license             text,
    comments            int8,
    likes               int8,
    views               int8,
    caption             boolean,
    content_rating      text,
    definition          text, -- 'hd', 'sd', others?
    dimensions          text, -- '2d', '3d'
    duration            text, -- 'PTnnMxxS', 'PTnnHnnMnnS'
    licensed            boolean,
    projection          text, -- 'rectangular','360', other?
    region_restrictions text,
    mpaa                text,
    mpaat               text,
    tvpg                text,
    yt_rating           text,  -- 'ytAgeRestricted'
    -- dislikes   int8
    -- favorites  int8
    -- privacy    text   -- 'public', 'unlisted', 'private'

    constraint video_pkey primary key (id)
);

-- note: the 'channel_id' is always "UCBR8-60-B28hp2BmDPdntcQ" (AFAIK)
create table video_category
(
    id          serial not null,
    category_id int4   not null,
    etag        text   not null,
    title       text,
    assignable  boolean,
    country     text,
    lang        text,

    constraint video_category_pkey primary key (id)
);

-- ------------------------------------------------------------
-- Secondary tables
-- ------------------------------------------------------------

create table tags
(
    video_id text not null,
    tag      text not null,
    original text not null
);

-- optimization
create table video_topic_deref
(
    video_id text not null,
    category text
);

-- ------------------------------------------------------------
-- Pre-initialized tables
-- ------------------------------------------------------------
create table category
(
    category_id integer not null,
    description text
);

-- aka 'topic category'
create table topic
(
    id     serial  not null,
    url    int4    not null,
    label  text    not null,
    custom boolean not null,

    constraint topic_pkey primary key (id)
);

-- mostly for reference...
create table thumbnail_size
(
    name   text not null, -- 'default', 'medium', 'high', 'standard', 'maxres'
    height int4 not null,
    width  int4 not null,

    constraint thumbnail_size_pkey primary key (name)
);

-- ------------------------------------------------------------
-- Cross-reference tables
-- ------------------------------------------------------------

create table channel_topic_xref
(
    channel_id  text not null,
    topic_id    int4 not null
);

create table video_topic_xref
(
    video_id text not null,
    topic_id int4 not null
);

-- ------------------------------------------------------------
-- thumbnails - may be outdated...
-- ------------------------------------------------------------
create table channel_thumbnail
(
    channel_id text not null,
    name       text not null,
    url        text not null,
    height     integer,
    width      integer
);

create table playlist_thumbnail
(
    playlist_id text not null,
    name        text not null,
    url         text not null,
    height      integer,
    width       integer
);

create table playlist_item_thumbnail
(
    playlist_item_id text not null,
    name             text not null,
    url              text not null,
    height           integer,
    width            integer
);

-- only videos support multiple thumbnail sizes
create table video_thumbnail
(
    video_id   text not null,
    name       text not null, -- 'default', 'medium', 'high', 'standard', 'maxres'
    url        text not null,
    -- height     int4,
    -- width      int4

    constraint video_thumbnail_pkey primary key (video_id, name)
);

-- ------------------------------------------------------------
-- Other
-- ------------------------------------------------------------
create table chrome_history
(
    video_id        text         not null,
    title           text,
    visit_count     integer      not null,
    typed_count     integer      not null,
    last_visit_time timestamp(3) not null
);

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
    published_at  timestamp(0) without time zone,
    tn_url        text,          -- https://yt3.ggpht.com/...
    country       text,
    lang          text,

    constraint channel_pkey primary key (id)
    -- constraint channel_etag_key unique(etag)
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
    published_at  timestamp(0) without time zone,
    tn_url        text, -- https://i.ytimg.com/vi/...
    lang          text,
    -- thumbnail_video_id text = always null?

    constraint playlist_pkey primary key (id)
    -- constraint playlist_etag_key unique(etag)
    -- constraint playlist_channel_fkey foreign key (channel_id) references channel(id)
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
    published_at       timestamp(0) without time zone,
    video_published_at timestamp(0) without time zone,
    position           int4,
    kind               text,          -- always 'youtube#video' ?

    constraint playlist_item_pkey primary key (id)
    -- constraint etag_key unique(etag)
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
    published_at        timestamp(0) without time zone,
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

-- only videos support multiple thumbnail sizes
create table video_thumbnail
(
    video_id   text not null,
    name       text not null, -- 'default', 'medium', 'high', 'standard', 'maxres'
    url        text not null,
    -- height     int4,
    -- width      int4

    constraint video_thumbnail_pkey primary key (video_id, name)
    -- constraint video_thumbnail_video_id_fkey foreign key (video_id) references video(id)
);

-- mostly for reference...
create table thumbnail_size
(
    name   text not null, -- 'default', 'medium', 'high', 'standard', 'maxres'
    height int4 not null,
    width  int4 not null,
    constraint thumbnail_size_pkey primary key (name)
);

create table channel_categories
(
    channel_id text not null,
    category   text
);

create table tags
(
    video_id text not null,
    tag      text not null,
    original text not null
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

    constraint video_category_pkey primary key (id),
    constraint video_category_category_id_key unique (category_id)
    -- constraint video_category_etag_key unique(etag)
);

-- cross-reference table
create table video_topic
(
    video_id text not null,
    topic_id int4 not null
);

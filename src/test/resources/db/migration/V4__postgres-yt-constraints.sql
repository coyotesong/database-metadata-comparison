alter table channel add constraint channel_etag_u unique(etag);
alter table playlist add constraint playlist_etag_u unique(etag);
alter table playlist_item add constraint playlist_item_etag_u unique(etag);
alter table video add constraint video_etag_u unique(etag);
alter table video_category add constraint video_category_etag_u unique(etag);

-- ------------------------------------------------------------
-- Primary tables
-- ------------------------------------------------------------

alter table playlist add constraint playlist_channel_fkey foreign key (channel_id) references channel(id);

alter table playlist_item add constraint playlist_item_playlist_fkey foreign key (playlist_id) references playlist(id);
alter table playlist_item add constraint playlist_item_channel_fkey foreign key (channel_id) references channel(id);
alter table playlist_item add constraint playlist_item_video_fkey foreign key (video_id) references video(id);

alter table video_category add constraint video_category_category_id_u unique (category_id);

alter table video add constraint video_channel_fkey foreign key (channel_id) references channel(id);
alter table video add constraint video_video_category_fkey foreign key (category_id) references video_category(category_id);

-- ------------------------------------------------------------

alter table tags add constraint tags_video_fkey foreign key (video_id) references video(id);

alter table video_topic_deref add constraint video_topic_video_id_fkey foreign key (video_id) references video(id);

-- ------------------------------------------------------------
-- Thumbmails
-- ------------------------------------------------------------
alter table channel_thumbnail add constraint channel_thumbnail_thumbnail_fkey foreign key (name) references thumbnail_size(name);
alter table channel_thumbnail add constraint channel_thumbnail_channel_id_fkey foreign key (channel_id) references video(id);

alter table playlist_thumbnail add constraint playlist_thumbnail_thumbnail_fkey foreign key (name) references thumbnail_size(name);
alter table playlist_thumbnail add constraint playlist_thumbnail_playlist_id_fkey foreign key (playlist_id) references video(id);

alter table playlist_item_thumbnail add constraint playlist_item_thumbnail_thumbnail_fkey foreign key (name) references thumbnail_size(name);
alter table playlist_item_thumbnail add constraint playlist_item_thumbnail_playlist_item_id_fkey foreign key (playlist_item_id) references video(id);

alter table video_thumbnail add constraint video_thumbnail_thumbnail_fkey foreign key (name) references thumbnail_size(name);
alter table video_thumbnail add constraint video_thumbnail_video_id_fkey foreign key (video_id) references video(id);

-- ------------------------------------------------------------
-- cross-reference tables
-- ------------------------------------------------------------

alter table channel_topic_xref add constraint channel_topic_xref_channel_id_fkey foreign key (channel_id) references channel(id);
alter table channel_topic_xref add constraint channel_topic_xref_topic_id_fkey foreign key (topic_id) references topic(id);

alter table video_topic_xref add constraint video_topic_xref_video_id_fkey foreign key (video_id) references video(id);
alter table video_topic_xref add constraint video_topic_xref_topic_id_fkey foreign key (topic_id) references topic(id);

-- alter table video_thumbnail add constraint video_thumbnail_video_id_fkey foreign key (id) references video(id);

-- alter table channel_categories add constraint channel_categories_fkey foreign key (channel_id) references channel(id);

-- alter table tags add constraint tags_video_fk foreign key (video) references video(id);

-- -- alter table video_category add constraint video_category_fkey 0xa...
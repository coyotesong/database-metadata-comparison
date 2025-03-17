alter table channel add constraint channel_etag_u unique(etag);
alter table playlist add constraint playlist_etag_u unique(etag);
alter table playlist_item add constraint playlist_item_etag_u unique(etag);
alter table video add constraint video_etag_u unique(etag);
alter table video_category add constraint video_category_etag_u unique(etag);

alter table playlist add constraint playlist_channel_fkey foreign key (channel_id) references channel(id);

alter table playlist_item add constraint playlist_item_playlist_fkey foreign key (playlist_id) references playlist(id);
alter table playlist_item add constraint playlist_item_channel_fkey foreign key (channel_id) references channel(id);

alter table video add constraint video_channel_fkey foreign key (channel_id) references channel(id);

alter table channel_categories add constraint channel_categories_channel_fkey foreign key (channel_id) references channel(id);

alter table video_topic add constraint video_topic_video_fkey foreign key (video_id) references video(id);
alter table video_topic add constraint video_topic_topic_fkey foreign key (topic_id) references topic(id);

alter table video_thumbnail add constraint video_category_video_fkey foreign key (video_id) references video(id);
alter table video_thumbnail add constraint video_thumbnail_thumbnail_fkey foreign key (name) references thumbnail_size(name);

alter table tags add constraint tags_video_fkey foreign key (video_id) references video(id);

alter table video add constraint video_video_category_fkey foreign key (category_id) references video_category(category_id);


-- alter table video_thumbnail add constraint video_thumbnail_video_id_fkey foreign key (id) references video(id);

-- alter table channel_categories add constraint channel_categories_fkey foreign key (channel_id) references channel(id);

-- alter table tags add constraint tags_video_fk foreign key (video) references video(id);

-- -- alter table video_category add constraint video_category_fkey 0xa...
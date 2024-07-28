package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.CommentCreateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> create(final long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> update(final long userId, Long itemId, ItemCreateDto itemCreateDto) {
        StringBuilder path = new StringBuilder("/").append(itemId);
        return patch(path.toString(), userId, itemCreateDto);
    }

    public ResponseEntity<Object> get(final long userId, Long itemId) {
        StringBuilder path = new StringBuilder("/").append(itemId);
        return get(path.toString(), userId);
    }

    public ResponseEntity<Object> getUserItems(final long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItem(String text) {
        StringBuilder path = new StringBuilder("/search").append("?text={text}");
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get(path.toString(), null, parameters);
    }

    public ResponseEntity<Object> addComment(final long userId, Long itemId, CommentCreateDto commentCreateDto) {
        StringBuilder path = new StringBuilder("/").append(itemId).append("/comment");
        return post(path.toString(), userId, commentCreateDto);
    }
}

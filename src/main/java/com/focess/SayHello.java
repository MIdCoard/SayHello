package com.focess;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SayHello implements DedicatedServerModInitializer {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    public void onInitializeServer() {

    }

    public static void onChat(ServerPlayerEntity player,String message) {
        if (Pattern.matches("!say ([\\s\\S]*)", message)){
            Matcher matcher = Pattern.compile("!say ([\\s\\S]*)").matcher(message);
            matcher.find();
            String chatContent = matcher.group(1);
            chatContent=chatContent.trim();
            String chatFrom = player.getDisplayName().asString();
            JsonObject dataJson = new JsonObject();
            dataJson.add("group_id", new JsonPrimitive(850093647));
            dataJson.add("auto_escape", new JsonPrimitive(true));
            dataJson.add("message", new JsonPrimitive("<" + chatFrom + "> " + chatContent));
            String result = post("http://127.0.0.1:5700/send_group_msg",dataJson.toString());
            if (result.contains("ok"))
                player.sendSystemMessage(new LiteralText("§2[喊话成功] §r" + message), UUID.randomUUID());
            else
                player.sendSystemMessage(new LiteralText("§4[喊话失败(未知原因)] §r" + message), UUID.randomUUID());
        }
    }

    public static String post(String url, String json) {
        RequestBody body = RequestBody.create(json,JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            return client.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "false";
    }
}

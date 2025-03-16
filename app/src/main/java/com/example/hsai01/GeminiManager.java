package com.example.hsai01;

import static com.example.hsai01.Keys.API_KEY;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.GenerateContentResponse;

import kotlin.Result;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;

public class GeminiManager {
    private static GeminiManager instance;
    private GenerativeModel gemini;

    private GeminiManager() {
        gemini = new GenerativeModel(
                "gemini-2.0-flash",
                API_KEY
        );
    }

    public static GeminiManager getInstance() {
        if (instance == null) {
            instance = new GeminiManager();
        }
        return instance;
    }

    public void sendMessage(String prompt, GeminiCallback callback) {
        gemini.generateContent(prompt,
                new Continuation<GenerateContentResponse>() {
                    @NonNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NonNull Object result) {
                        if (result instanceof Result.Failure) {
                            callback.onError(((Result.Failure) result).exception);
                        } else {
                            callback.onSuccess(((GenerateContentResponse) result).getText());
                        }
                    }
                }
        );
    }

    public void sendMessageWithPhoto(String prompt, Bitmap photo, GeminiCallback callback) {
        gemini.generateContent(prompt,
                new Continuation<GenerateContentResponse>() {
                    @NonNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NonNull Object result) {
                        if (result instanceof Result.Failure) {
                            callback.onError(((Result.Failure) result).exception);
                        } else {
                            callback.onSuccess(((GenerateContentResponse) result).getText());
                        }
                    }
                }
        );
    }
}


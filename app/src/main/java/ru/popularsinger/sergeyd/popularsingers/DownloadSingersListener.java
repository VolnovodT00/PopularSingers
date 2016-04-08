package ru.popularsinger.sergeyd.popularsingers;

/**
 * Created by sergeyd on 04/07/2016.
 */
public interface DownloadSingersListener
{
    // начинаем скачивание
    void onBegin();
    // процесс скачивания
    void onProgress(Integer state, Integer value);
    // получили ошибку
    void onFailure(Integer code);
    // закончили
    void onEnd();
}

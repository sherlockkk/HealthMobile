#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <android/log.h>
#include <unistd.h>
#include <sys/inotify.h>

#include "com_alpha_healthmobile_uninstall_UninstallApp.h"

/* �궨��begin */
//��0��
#define MEM_ZERO(pDest, destSize) memset(pDest, 0, destSize)

#define LOG_TAG "onEvent"

//LOG�궨��
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)

JNIEXPORT jstring JNICALL Java_com_alpha_healthmobile_uninstall_UninstallApp_Uninstall(JNIEnv* env, jobject thiz) {

    //��ʼ��log
    LOGD("init start...");

    //fork�ӽ��̣���ִ����ѯ����
    pid_t pid = fork();
    if (pid < 0) {
        //����log
        LOGD("fork failed...");
    } else if (pid == 0) {
        //�ӽ���ע��"/data/data/com.example.uninstallprompt"Ŀ¼������
        int fileDescriptor = inotify_init();
        if (fileDescriptor < 0) {
            LOGD("inotify_init failed...");
            exit(1);
        }

        int watchDescriptor;
        watchDescriptor = inotify_add_watch(fileDescriptor,"/data/data/com.alpha.healthmobile", IN_DELETE);
        LOGD("watchDescriptor=%d",watchDescriptor);
        if (watchDescriptor < 0) {
            LOGD("inotify_add_watch failed...");
            exit(1);
        }

        //���仺�棬�Ա��ȡevent�������С=һ��struct inotify_event�Ĵ�С������һ�δ���һ��event
        void *p_buf = malloc(sizeof(struct inotify_event));
        if (p_buf == NULL) {
            LOGD("malloc failed...");
            exit(1);
        }
        //��ʼ����
        LOGD("start observer...");
        size_t readBytes = read(fileDescriptor, p_buf,sizeof(struct inotify_event));

        //read���������̣��ߵ�����˵���յ�Ŀ¼��ɾ�����¼���ע��������
        free(p_buf);
        inotify_rm_watch(fileDescriptor, IN_DELETE);

        //Ŀ¼������log
        LOGD("uninstall");

        //ִ������am start -a android.intent.action.VIEW -d http://shouji.360.cn/web/uninstall/uninstall.html
        //execlp(
        //  "am", "am", "start", "-a", "android.intent.action.VIEW", "-d",
        //  "http://shouji.360.cn/web/uninstall/uninstall.html", (char *)NULL);
        //4.2���ϵ�ϵͳ�����û�Ȩ�޹�����ϸ���Ҫ���� --user 0
        execlp("am", "am", "start", "--user", "0", "-a",
        "android.intent.action.VIEW", "-d", "http://atgn.cn/",(char *) NULL);

    } else {
        //������ֱ���˳���ʹ�ӽ��̱�init�����������Ա����ӽ��̽���
    }

    return (*env)->NewStringUTF(env, "Hello from JNI !");
}

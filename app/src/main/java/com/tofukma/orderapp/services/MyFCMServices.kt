package com.tofukma.orderapp.services

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tofukma.orderapp.Utils.Common
import java.util.*

class MyFCMServices : FirebaseMessagingService(){
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Common.updateToken(this,p0)
        // Khi một thiết bị cài đặt ứng dụng thì nó sẽ tạo ra một device_token, ta sẽ gửi device_token đó lên Firebase
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val dataRev = remoteMessage.data
        if(dataRev != null ){
           if (dataRev[Common.IS_SEND_IMAGE] != null && dataRev[Common.IS_SEND_IMAGE].equals("true")){
            Glide.with(this)
                .asBitmap()
                .load(dataRev[Common.IMAGE_URL])
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        Common.showNotification(this@MyFCMServices,
                            Random().nextInt(),
                            dataRev[Common.NOTI_TITLE],
                            dataRev[Common.NOTI_CONTENT],
                            resource,
                            null)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        TODO("Not yet implemented")
                    }
                })


           }else{

               Common.showNotification(this,
                   Random().nextInt(),
                   dataRev[Common.NOTI_TITLE],
                   dataRev[Common.NOTI_CONTENT],null)

           }
        }


    }


}
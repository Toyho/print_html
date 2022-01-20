package com.example.print_html

import android.content.*
import io.flutter.embedding.android.FlutterActivity
import androidx.annotation.NonNull
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.IBinder
import io.flutter.Log


import ru.istapel.printhtml.IPrintHtmlAIDL


class MainActivity: FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/battery"


    var iPrintHtmlAIDL: IPrintHtmlAIDL? = null

    val serviceConnectionUpos = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            iPrintHtmlAIDL = IPrintHtmlAIDL.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(className: ComponentName) {
            iPrintHtmlAIDL = null
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("onStart", "onStart")

        println("onStart")
        val packageNamePrintHtml = "ru.istapel.printhtml"
        val patchServicePrintHtml = "ru.istapel.printhtml.service.PrintHtmlAidlService"
        val ACTION_PRINT_HTML_AIDL = "ru.istapel.printhtml.IPrintHtmlAIDL"

        val printHtmlIntent = Intent(ACTION_PRINT_HTML_AIDL)
        printHtmlIntent.component = ComponentName(packageNamePrintHtml, patchServicePrintHtml)
        bindService(printHtmlIntent, serviceConnectionUpos, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnectionUpos)
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        Log.d("configureFlutterEngine", "configureFlutterEngine")

        println("configureFlutterEngine")

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->

            if (call.method == "createCheck") {

                println("createCheck")
                Log.d("createCheck", "createCheck")

                iPrintHtmlAIDL!!.printHtml("dugvhsdfouivghsiouf")

                println("close createCheck")

            }

            if (call.method == "getBatteryLevel") {
                val batteryLevel = getBatteryLevel()
                    iPrintHtmlAIDL!!.printHtml("dugvhsdfouivghsiouf")
                    result.success(
                        if (iPrintHtmlAIDL != null) {
                            "true"
                        } else {
                            "false"
                        }
                    )


                /*if (batteryLevel != -1) {
                    result.success(batteryLevel)
                } else {
                    result.error("UNAVAILABLE", "Battery level not available.", null)
                }*/
            } else {
                result.notImplemented()
            }



        }
    }

    private fun getBatteryLevel(): Int {

//        val packageNamePrintHtml = "ru.istapel.printhtml"
//        val patchServicePrintHtml = "ru.istapel.printhtml.service.PrintHtmlAidlService"
//        val ACTION_PRINT_HTML_AIDL = "ru.istapel.printhtml.IPrintHtmlAIDL"
//
//        val printHtmlIntent = Intent(ACTION_PRINT_HTML_AIDL)
//        printHtmlIntent.component = ComponentName(packageNamePrintHtml, patchServicePrintHtml)
//        bindService(printHtmlIntent, serviceConnectionUpos, Context.BIND_AUTO_CREATE)

        return if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            val batteryManager = getSystemService(BATTERY_SERVICE) as BatteryManager
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        } else {
            val intent = ContextWrapper(applicationContext).registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(
                BatteryManager.EXTRA_SCALE,
                -1
            )
        }
    }

}

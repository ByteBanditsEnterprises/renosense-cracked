//Raddon On Top!

package com.sun.jna.platform.win32;

import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.concurrent.*;
import java.lang.reflect.*;

public final class User32Util
{
    public static final EnumSet<Win32VK> WIN32VK_MAPPABLE;
    
    public static final int registerWindowMessage(final String lpString) {
        final int messageId = User32.INSTANCE.RegisterWindowMessage(lpString);
        if (messageId == 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return messageId;
    }
    
    public static final WinDef.HWND createWindow(final String className, final String windowName, final int style, final int x, final int y, final int width, final int height, final WinDef.HWND parent, final WinDef.HMENU menu, final WinDef.HINSTANCE instance, final WinDef.LPVOID param) {
        return createWindowEx(0, className, windowName, style, x, y, width, height, parent, menu, instance, param);
    }
    
    public static final WinDef.HWND createWindowEx(final int exStyle, final String className, final String windowName, final int style, final int x, final int y, final int width, final int height, final WinDef.HWND parent, final WinDef.HMENU menu, final WinDef.HINSTANCE instance, final WinDef.LPVOID param) {
        final WinDef.HWND hWnd = User32.INSTANCE.CreateWindowEx(exStyle, className, windowName, style, x, y, width, height, parent, menu, instance, param);
        if (hWnd == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return hWnd;
    }
    
    public static final void destroyWindow(final WinDef.HWND hWnd) {
        if (!User32.INSTANCE.DestroyWindow(hWnd)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }
    
    public static final List<WinUser.RAWINPUTDEVICELIST> GetRawInputDeviceList() {
        final IntByReference puiNumDevices = new IntByReference(0);
        final WinUser.RAWINPUTDEVICELIST placeholder = new WinUser.RAWINPUTDEVICELIST();
        final int cbSize = placeholder.sizeof();
        int returnValue = User32.INSTANCE.GetRawInputDeviceList((WinUser.RAWINPUTDEVICELIST[])null, puiNumDevices, cbSize);
        if (returnValue != 0) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        final int deviceCount = puiNumDevices.getValue();
        final WinUser.RAWINPUTDEVICELIST[] records = (WinUser.RAWINPUTDEVICELIST[])placeholder.toArray(deviceCount);
        returnValue = User32.INSTANCE.GetRawInputDeviceList(records, puiNumDevices, cbSize);
        if (returnValue == -1) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        if (returnValue != records.length) {
            throw new IllegalStateException("Mismatched allocated (" + records.length + ") vs. received devices count (" + returnValue + ")");
        }
        return Arrays.asList(records);
    }
    
    public static String loadString(final String location) throws UnsupportedEncodingException {
        int x = location.lastIndexOf(44);
        final String moduleName = location.substring(0, x);
        final int index = Math.abs(Integer.parseInt(location.substring(x + 1)));
        final String path = Kernel32Util.expandEnvironmentStrings(moduleName);
        final WinDef.HMODULE target = Kernel32.INSTANCE.LoadLibraryEx(path, (WinNT.HANDLE)null, 2);
        if (target == null) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        final Pointer lpBuffer = (Pointer)new Memory((long)Native.POINTER_SIZE);
        x = User32.INSTANCE.LoadString((WinDef.HINSTANCE)target, index, lpBuffer, 0);
        if (0 == x) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
            return new String(lpBuffer.getPointer(0L).getCharArray(0L, x));
        }
        return new String(lpBuffer.getPointer(0L).getByteArray(0L, x), Native.getDefaultStringEncoding());
    }
    
    static {
        WIN32VK_MAPPABLE = EnumSet.of(Win32VK.VK_BACK, Win32VK.VK_TAB, Win32VK.VK_CLEAR, Win32VK.VK_RETURN, Win32VK.VK_ESCAPE, Win32VK.VK_SPACE, Win32VK.VK_SELECT, Win32VK.VK_EXECUTE, Win32VK.VK_0, Win32VK.VK_1, Win32VK.VK_2, Win32VK.VK_3, Win32VK.VK_4, Win32VK.VK_5, Win32VK.VK_6, Win32VK.VK_7, Win32VK.VK_8, Win32VK.VK_9, Win32VK.VK_A, Win32VK.VK_B, Win32VK.VK_C, Win32VK.VK_D, Win32VK.VK_E, Win32VK.VK_F, Win32VK.VK_G, Win32VK.VK_H, Win32VK.VK_I, Win32VK.VK_J, Win32VK.VK_K, Win32VK.VK_L, Win32VK.VK_M, Win32VK.VK_N, Win32VK.VK_O, Win32VK.VK_P, Win32VK.VK_Q, Win32VK.VK_R, Win32VK.VK_S, Win32VK.VK_T, Win32VK.VK_U, Win32VK.VK_V, Win32VK.VK_W, Win32VK.VK_X, Win32VK.VK_Y, Win32VK.VK_Z, Win32VK.VK_NUMPAD0, Win32VK.VK_NUMPAD1, Win32VK.VK_NUMPAD2, Win32VK.VK_NUMPAD3, Win32VK.VK_NUMPAD4, Win32VK.VK_NUMPAD5, Win32VK.VK_NUMPAD6, Win32VK.VK_NUMPAD7, Win32VK.VK_NUMPAD8, Win32VK.VK_NUMPAD9, Win32VK.VK_MULTIPLY, Win32VK.VK_ADD, Win32VK.VK_SEPARATOR, Win32VK.VK_SUBTRACT, Win32VK.VK_DECIMAL, Win32VK.VK_DIVIDE, Win32VK.VK_OEM_NEC_EQUAL, Win32VK.VK_OEM_FJ_MASSHOU, Win32VK.VK_OEM_FJ_TOUROKU, Win32VK.VK_OEM_FJ_LOYA, Win32VK.VK_OEM_FJ_ROYA, Win32VK.VK_OEM_1, Win32VK.VK_OEM_PLUS, Win32VK.VK_OEM_COMMA, Win32VK.VK_OEM_MINUS, Win32VK.VK_OEM_PERIOD, Win32VK.VK_OEM_2, Win32VK.VK_OEM_3, Win32VK.VK_RESERVED_C1, Win32VK.VK_RESERVED_C2, Win32VK.VK_OEM_4, Win32VK.VK_OEM_5, Win32VK.VK_OEM_6, Win32VK.VK_OEM_7, Win32VK.VK_OEM_8, Win32VK.VK_OEM_AX, Win32VK.VK_OEM_102, Win32VK.VK_ICO_HELP, Win32VK.VK_PROCESSKEY, Win32VK.VK_ICO_CLEAR, Win32VK.VK_PACKET, Win32VK.VK_OEM_RESET, Win32VK.VK_OEM_JUMP, Win32VK.VK_OEM_PA1, Win32VK.VK_OEM_PA2, Win32VK.VK_OEM_PA3, Win32VK.VK_OEM_WSCTRL, Win32VK.VK_OEM_CUSEL, Win32VK.VK_OEM_ATTN, Win32VK.VK_OEM_FINISH, Win32VK.VK_OEM_COPY, Win32VK.VK_OEM_AUTO, Win32VK.VK_OEM_ENLW, Win32VK.VK_OEM_BACKTAB, Win32VK.VK_ATTN, Win32VK.VK_CRSEL, Win32VK.VK_EXSEL, Win32VK.VK_EREOF, Win32VK.VK_PLAY, Win32VK.VK_ZOOM, Win32VK.VK_NONAME, Win32VK.VK_PA1, Win32VK.VK_OEM_CLEAR);
    }
    
    public static class MessageLoopThread extends Thread
    {
        private volatile int nativeThreadId;
        private volatile long javaThreadId;
        private final List<FutureTask> workQueue;
        private static long messageLoopId;
        
        public MessageLoopThread() {
            this.nativeThreadId = 0;
            this.javaThreadId = 0L;
            this.workQueue = (List<FutureTask>)Collections.synchronizedList(new ArrayList<FutureTask>());
            this.setName("JNA User32 MessageLoop " + ++MessageLoopThread.messageLoopId);
        }
        
        @Override
        public void run() {
            final WinUser.MSG msg = new WinUser.MSG();
            User32.INSTANCE.PeekMessage(msg, (WinDef.HWND)null, 0, 0, 0);
            this.javaThreadId = Thread.currentThread().getId();
            this.nativeThreadId = Kernel32.INSTANCE.GetCurrentThreadId();
            int getMessageReturn;
            while ((getMessageReturn = User32.INSTANCE.GetMessage(msg, (WinDef.HWND)null, 0, 0)) != 0) {
                if (getMessageReturn != -1) {
                    while (!this.workQueue.isEmpty()) {
                        try {
                            final FutureTask ft = this.workQueue.remove(0);
                            ft.run();
                            continue;
                        }
                        catch (IndexOutOfBoundsException ex) {}
                        break;
                    }
                    User32.INSTANCE.TranslateMessage(msg);
                    User32.INSTANCE.DispatchMessage(msg);
                }
                else {
                    if (this.getMessageFailed()) {
                        break;
                    }
                    continue;
                }
            }
            while (!this.workQueue.isEmpty()) {
                this.workQueue.remove(0).cancel(false);
            }
        }
        
        public <V> Future<V> runAsync(final Callable<V> command) {
            while (this.nativeThreadId == 0) {
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            final FutureTask<V> futureTask = new FutureTask<V>(command);
            this.workQueue.add(futureTask);
            User32.INSTANCE.PostThreadMessage(this.nativeThreadId, 1024, (WinDef.WPARAM)null, (WinDef.LPARAM)null);
            return futureTask;
        }
        
        public <V> V runOnThread(final Callable<V> callable) throws Exception {
            while (this.javaThreadId == 0L) {
                try {
                    Thread.sleep(20L);
                }
                catch (InterruptedException ex) {
                    Logger.getLogger(MessageLoopThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (this.javaThreadId == Thread.currentThread().getId()) {
                return callable.call();
            }
            final Future<V> ft = this.runAsync(callable);
            try {
                return ft.get();
            }
            catch (InterruptedException ex2) {
                throw ex2;
            }
            catch (ExecutionException ex3) {
                final Throwable cause = ex3.getCause();
                if (cause instanceof Exception) {
                    throw (Exception)cause;
                }
                throw ex3;
            }
        }
        
        public void exit() {
            User32.INSTANCE.PostThreadMessage(this.nativeThreadId, 18, (WinDef.WPARAM)null, (WinDef.LPARAM)null);
        }
        
        protected boolean getMessageFailed() {
            final int lastError = Kernel32.INSTANCE.GetLastError();
            Logger.getLogger("com.sun.jna.platform.win32.User32Util.MessageLoopThread").log(Level.WARNING, "Message loop was interrupted by an error. [lastError: {0}]", lastError);
            return true;
        }
        
        static {
            MessageLoopThread.messageLoopId = 0L;
        }
        
        public class Handler implements InvocationHandler
        {
            private final Object delegate;
            
            public Handler(final Object delegate) {
                this.delegate = delegate;
            }
            
            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                try {
                    return MessageLoopThread.this.runOnThread((Callable<Object>)new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            return method.invoke(Handler.this.delegate, args);
                        }
                    });
                }
                catch (InvocationTargetException ex) {
                    final Throwable cause = ex.getCause();
                    if (cause instanceof Exception) {
                        final StackTraceElement[] hiddenStack = cause.getStackTrace();
                        cause.fillInStackTrace();
                        final StackTraceElement[] currentStack = cause.getStackTrace();
                        final StackTraceElement[] fullStack = new StackTraceElement[currentStack.length + hiddenStack.length];
                        System.arraycopy(hiddenStack, 0, fullStack, 0, hiddenStack.length);
                        System.arraycopy(currentStack, 0, fullStack, hiddenStack.length, currentStack.length);
                        cause.setStackTrace(fullStack);
                        throw (Exception)cause;
                    }
                    throw ex;
                }
            }
        }
    }
}

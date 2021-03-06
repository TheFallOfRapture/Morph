package com.morph.engine.input

typealias Action = () -> Unit

class InputMapping {
    private val keyPressed = HashMap<Int, Action>()
    private val keyReleased = HashMap<Int, Action>()
    private val keyTyped = HashMap<Int, Action>()

    private val mousePressed = HashMap<Int, Action>()
    private val mouseReleased = HashMap<Int, Action>()

    private val keyUp = HashMap<Int, Action>()
    private val keyDown = HashMap<Int, Action>()

    private val mouseUp = HashMap<Int, Action>()
    private val mouseDown = HashMap<Int, Action>()

    private val mouseDownCombo = HashMap<IntArray, Action>()

    fun update() {
        Keyboard.standardKeyEvents.forEach { acceptStd(it) }
        Mouse.standardMouseEvents.forEach { acceptStd(it) }

        Keyboard.binaryKeyEvents.forEach { acceptLong(it) }
        Mouse.binaryMouseEvents.forEach { acceptLong(it) }
    }

    // JVM Compatibility Methods
    fun mapButton(button: Int, mouseAction: MouseActions, action: Runnable) = mapButton(button, mouseAction.action) { action.run() }
    fun mapKey(key: Int, keyAction: KeyActions, action: Runnable) = mapKey(key, keyAction.action) { action.run() }

    fun mapButton(button: Int, mouseAction: MouseAction, action: Action) = when (mouseAction) {
        is StdMouseAction -> when (mouseAction) {
            MousePress -> mousePressed.put(button, action)
            MouseRelease -> mouseReleased.put(button, action)
        }
        is BinMouseAction -> when (mouseAction) {
            MouseUp -> mouseUp.put(button, action)
            MouseDown -> mouseDown.put(button, action)
        }
    }

    fun mapKey(key: Int, keyAction: KeyAction, action: Action) = when (keyAction) {
        is StdKeyAction -> when (keyAction) {
            KeyPress -> keyPressed.put(key, action)
            KeyRepeat -> {
                keyPressed[key] = action; keyTyped.put(key, action) }
            KeyRelease -> keyReleased.put(key, action)
        }
        is BinKeyAction -> when (keyAction) {
            KeyUp -> keyUp.put(key, action)
            KeyDown -> keyDown.put(key, action)
        }
    }

    private fun getMapByAction(action: StdMouseAction): HashMap<Int, Action> = when (action) {
        MousePress -> mousePressed
        MouseRelease -> mouseReleased
    }

    private fun getMapByLongAction(action: BinMouseAction): HashMap<Int, Action> = if (action == MouseUp) mouseUp else mouseDown
    private fun getMapByLongAction(action: BinKeyAction): HashMap<Int, Action> = if (action == KeyUp) keyUp else keyDown

    private fun acceptStd(mouseEvent: StdMouseEvent) = getMapByAction(mouseEvent.action).getOrDefault(mouseEvent.button) { }()
    private fun acceptStd(keyEvent: StdKeyEvent) = when (keyEvent.action) {
        KeyPress -> {
            (keyPressed).getOrDefault(keyEvent.key) { }()
            (keyTyped).getOrDefault(keyEvent.key) { }()
        }
        KeyRepeat -> (keyTyped).getOrDefault(keyEvent.key) { }()
        KeyRelease -> (keyReleased).getOrDefault(keyEvent.key) { }()
    }

    private fun acceptLong(mouseEvent: BinMouseEvent) = getMapByLongAction(mouseEvent.action).getOrDefault(mouseEvent.button) { }()
    private fun acceptLong(keyEvent: BinKeyEvent) = getMapByLongAction(keyEvent.action).getOrDefault(keyEvent.key) { }()
}
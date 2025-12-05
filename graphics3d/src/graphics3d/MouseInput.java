package graphics3d;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private Vector2f currentPos;
    private Vector2f displVec;
    private float scrollx, scrolly;
    private boolean scrolling;
    private boolean activescroll;
    private boolean inWindow;
    private boolean leftButtonPressed;
    private Vector2f previousPos;
    private boolean rightButtonPressed;
	private boolean mouseCapture;
	private int ignoreMouse;
	private long handle;
    
    static float SCROLL_SLOWDOWN = 0.90f;
    static float SCROLL_STOP = 0.00001f;

    public MouseInput(long handle) {
    	this.handle = handle;
        previousPos = new Vector2f(-1, -1);
        currentPos = new Vector2f();
        displVec = new Vector2f();
        scrollx = 0;
        scrolly = 0;
        scrolling = false;
        activescroll = false;
        leftButtonPressed = false;
        rightButtonPressed = false;
        inWindow = false;

        glfwSetCursorPosCallback(handle, (_, xpos, ypos) -> {
            currentPos.x = (float) xpos;
            currentPos.y = (float) ypos;
            if (ignoreMouse > 0) ignoreMouse -= 1;
        });
        glfwSetCursorEnterCallback(handle, (_, entered) -> inWindow = entered);
        glfwSetMouseButtonCallback(handle, (_, button, action, _) -> {
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
        
        glfwSetScrollCallback(handle, (_, xscroll, yscroll) -> {
        	scrollx = (float) xscroll;
        	scrolly = (float) yscroll;
        	scrolling = true;
        	activescroll = true;
        });
    }

    public Vector2f getCurrentPos() {
        return currentPos;
    }

    public Vector2f getDisplVec() {
        return displVec;
    }
    
    public float getScrollY() {
    	return scrolly;
    }
    
    public float getScrollX() {
    	return scrollx;
    }

    public void input() {
        displVec.x = 0;
        displVec.y = 0;
        if (ignoreMouse == 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float) deltax;
            }
            if (rotateY) {
                displVec.x = (float) deltay;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
        
        if (!scrolling && activescroll) {
        	scrollx *= SCROLL_SLOWDOWN;
        	scrolly *= SCROLL_SLOWDOWN;
        	if (scrollx <= SCROLL_STOP && scrolly <= SCROLL_STOP) {
        		activescroll = false;
        		scrollx = 0;
        		scrolly = 0;
        	}
        }
        scrolling = false;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }
    
    public void captureMouse() {
		if (!mouseCapture)
			glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED | GLFW_CURSOR_HIDDEN);
		mouseCapture = true;
	}
	
	public void freeMouse() {
		if (mouseCapture) {
			glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			ignoreMouse = 2;
		}
		mouseCapture = false;
	}
	
	public boolean captured() {
		return mouseCapture;
	}
}

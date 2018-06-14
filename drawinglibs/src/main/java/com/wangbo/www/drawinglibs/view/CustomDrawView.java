package com.wangbo.www.drawinglibs.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;
import com.wangbo.www.drawinglibs.R;
import com.wangbo.www.drawinglibs.adapter.PictureAdapter;
import com.wangbo.www.drawinglibs.config.NoteApplication;
import com.wangbo.www.drawinglibs.ipm.ModeSelectCallBack;
import com.wangbo.www.drawinglibs.state.BaseState;
import com.wangbo.www.drawinglibs.state.CircleState;
import com.wangbo.www.drawinglibs.state.EraserState;
import com.wangbo.www.drawinglibs.state.LineState;
import com.wangbo.www.drawinglibs.state.PathState;
import com.wangbo.www.drawinglibs.state.RectangleState;
import com.wangbo.www.drawinglibs.utils.CommandUtils;
import com.wangbo.www.drawinglibs.utils.DrawDataUtils;
import com.wangbo.www.drawinglibs.utils.FileUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

public class CustomDrawView extends LinearLayout implements View.OnClickListener, ColorPicker.OnColorChangedListener, ModeSelectCallBack, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemClickListener{
    //初始化对话框
    private AppCompatDialog mAppCompatDialog;
    private VerticalSeekBar mVerticalSeekBar;
    private DrawView mDrawView;
    private TextView mTVSelectMode;
    private TextView mTVPageSize;
    private String mPicturePath = null;
    private int mPageSize = 1,wTime,hTime;
    private ModeSelectWindow mModeSelectWindow;
    private ActionBarDrawerToggle mDrawerToggle;
    private PictureAdapter mPageAdapter;
    private List<String> list = null;
    private AlertDialog.Builder mBuilder;
    private View mView;
    private FreeScrollView mFreeScollView;
    private PaperContainer mPaperContainer;
    private  BaseState baseState;
    private Context mContext;
    private boolean openBottomBar,openTopBar;
    private View bottombar,topBar;
    private String flags="1";

    public void setFlags(String flags) {
        this.flags = flags;
    }

    public CustomDrawView(Context context) {
        this(context,null);
    }

    public CustomDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.CustomDrawView);
        openBottomBar=typedArray.getBoolean(R.styleable.CustomDrawView_openBottomBar,false);
        wTime=typedArray.getInt(R.styleable.CustomDrawView_widthLine,1);
        hTime=typedArray.getInt(R.styleable.CustomDrawView_heightLine,1);
        openTopBar=typedArray.getBoolean(R.styleable.CustomDrawView_openTopBar,false);
        initView();
    }
    private void initView(){
        list = new ArrayList<>();
        mView= LayoutInflater.from(getContext()).inflate(R.layout.layout_draw,this);
        mDrawView = mView.findViewById(R.id.draw_view);
        mDrawView.initParameter(wTime,hTime);
        bottombar=findViewById(R.id.bottombar);
        bottombar.setVisibility(openBottomBar?VISIBLE:GONE);
        mVerticalSeekBar=findViewById(R.id.seekBar);
        mVerticalSeekBar.setVisibility(openBottomBar?VISIBLE:GONE);
        topBar=findViewById(R.id.toolbar);
        topBar.setVisibility(openTopBar?VISIBLE:GONE);
        //初始化颜色板
        initColorPickerDialog();
        //初始化自定义的ToolBar
        initToolbar();
        mVerticalSeekBar.setOnSeekBarChangeListener(this);
        mDrawView.changePaintSize(mVerticalSeekBar.getProgress());
        //初始化TAB
        initNavigationTab();
        //初始化模式选择的PopupWindow
        initModeSelectWindow();
        initDialog();
        initDrawData();
    }
    private void initDrawData() {
        if(NoteApplication.noteMap.get(flags)==null)
            return;
        mPicturePath =NoteApplication.noteMap.get(flags)+".png";
        String filePath ="null";//NoteApplication.ROOT_DIRECTORY+"/20180614114826.png";// NoteApplication.noteMap.get("1");
        if (mPicturePath != null) {
            Log.e("lichaojian--path",mPicturePath);
            String xmlPath = mPicturePath.substring(0, mPicturePath.length() - 3) + "xml";
            DrawDataUtils.getInstance().structureReReadXMLData("file://" + xmlPath);
            mDrawView.drawFromData();
            mDrawView.addCommand();
            if (!filePath.equals("null") && filePath != null) {
                File file = new File(filePath);
                File[] allFiles = file.listFiles();
                for (int i = 0; i < allFiles.length; ++i) {
                    if (allFiles[i].getPath().contains("png")) {
                        list.add(allFiles[i].getPath());
                    }
                }
            }
        }

        mPageSize = list.size();
        if (mPageSize == 0) {
            mPageSize = 1;
        }
        mTVPageSize.setText(Integer.toString(mPageSize));
    }
    private void initDialog() {
        mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("提示");
        mBuilder.setMessage("是否保存当前画布");
        mBuilder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });
        mBuilder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }
    private void initModeSelectWindow() {
        mModeSelectWindow = new ModeSelectWindow(getContext(), R.layout.mode_popuwindow);
        mModeSelectWindow.setModeSelectCallBack(this);
    }
    /**
     * 初始化导航Tab
     */
    private void initNavigationTab() {
        findViewById(R.id.rl_color_select_dialog).setOnClickListener(this);
        findViewById(R.id.rl_pencil_menu_select).setOnClickListener(this);
        findViewById(R.id.rl_mode_select).setOnClickListener(this);
        mTVSelectMode = (TextView) findViewById(R.id.tv_select_mode);
        findViewById(R.id.rl_shear).setOnClickListener(this);
        findViewById(R.id.rl_hard_eraser).setOnClickListener(this);
    }
    /**
     * 初始化一个颜色选择板块
     */
    private void initColorPickerDialog() {
        mAppCompatDialog = new AppCompatDialog(getContext());
        mAppCompatDialog.setContentView(R.layout.dialog_color_picker);
        ColorPicker colorPicker = (ColorPicker) mAppCompatDialog.findViewById(R.id.picker);
        SVBar svBar = (SVBar) mAppCompatDialog.findViewById(R.id.sv_bar);
        OpacityBar opacityBar = (OpacityBar) mAppCompatDialog.findViewById(R.id.opacity_bar);
        SaturationBar saturationBar = (SaturationBar) mAppCompatDialog.findViewById(R.id.saturation_bar);//饱和度
        ValueBar valueBar = (ValueBar) mAppCompatDialog.findViewById(R.id.value_bar);
        colorPicker.addSVBar(svBar);
        colorPicker.addOpacityBar(opacityBar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addValueBar(valueBar);

        colorPicker.getColor();
        colorPicker.setOldCenterColor(colorPicker.getColor());
        colorPicker.setOnColorChangedListener(this);
        colorPicker.setShowOldCenterColor(false);
    }
    /**
     * 初始化自定义toolbar
     */
    private void initToolbar() {
        mView.findViewById(R.id.ll_back).setOnClickListener(this);
        mView.findViewById(R.id.ll_num).setOnClickListener(this);
        mView.findViewById(R.id.ll_add).setOnClickListener(this);
        mView.findViewById(R.id.ll_undo).setOnClickListener(this);
        mView.findViewById(R.id.ll_redo).setOnClickListener(this);
        mView.findViewById(R.id.ll_reset).setOnClickListener(this);
        mView.findViewById(R.id.ll_save).setOnClickListener(this);
        mTVPageSize = findViewById(R.id.tv_page_size);
        mFreeScollView = mView.findViewById(R.id.freeScrollView);
        mFreeScollView.setSmoothScrollingEnabled(true);
        mFreeScollView.setFlingEnabled(true);
        mPaperContainer = mView.findViewById(R.id.paperContainer);
        mPaperContainer.initSize((Activity)(mContext),wTime,hTime);
        mPaperContainer.setOnTouchListener(new DraggablePaperTouchListener(mFreeScollView, mDrawView));
    }

    /**
     * 保存当前图片
     */
    private void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String fileName = mDrawView.save(mPicturePath, NoteApplication.ROOT_DIRECTORY);
                NoteApplication.noteMap.put(flags,fileName);
            }
        }).start();
//
//        if (DrawDataUtils.getInstance().getSaveDrawDataList().size() > 0 && fileName != null) {
//            Intent intent = new Intent();
//            intent.putExtra("path", NoteApplication.ROOT_DIRECTORY + "/" + fileName + ".png");
//            intent.putExtra("filePath", "null");
//            if (mPicturePath == null) {
//            } else {
//            }
//        } else {
//        }
        mPicturePath = null;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ll_back) {
            mBuilder.show();

        }  else if (i == R.id.ll_add) {
            mTVPageSize.setText(Integer.toString(++mPageSize));
            String fileName = mDrawView.save(mPicturePath, NoteApplication.TEMPORARY_PATH);
            list.add(NoteApplication.TEMPORARY_PATH + "/" + fileName + ".png");
            mDrawView.setCanvasCode(NoteApplication.CANVAS_RESET);
            mDrawView.invalidate();
            dataReset();

        } else if (i == R.id.ll_undo) {
            mDrawView.setCanvasCode(NoteApplication.CANVAS_UNDO);
            mDrawView.invalidate();

        } else if (i == R.id.ll_redo) {
            mDrawView.setCanvasCode(NoteApplication.CANVAS_REDO);
            mDrawView.invalidate();

        } else if (i == R.id.ll_reset) {// TODO: 2016/8/8 在此处弹出dialog让用户确认清空画布
            mDrawView.setCanvasCode(NoteApplication.CANVAS_RESET);
            mDrawView.invalidate();
            dataReset();

        } else if (i == R.id.ll_save) {
           getperr();

        } else if (i == R.id.rl_pencil_menu_select) {
            if(baseState==null)
                return;
            mDrawView.setCurrentState(baseState);
        } else if (i == R.id.rl_color_select_dialog) {
            mAppCompatDialog.show();

        } else if (i == R.id.rl_mode_select) {
            mModeSelectWindow.showPopupWindow(v);

        } else if (i == R.id.rl_shear) {//mDrawView.setCurrentState(ShearState.getInstance(mDrawView));

        } else if (i == R.id.rl_hard_eraser) {
            mDrawView.setCurrentState(EraserState.getInstance());

        } else {
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mTVPageSize.setText(Integer.toString(position + 1));
        String pngPath = list.get(position);
        String xmlPath = pngPath.substring(0, pngPath.length() - 3) + "xml";
        mDrawView.reset();
        dataReset();
        DrawDataUtils.getInstance().structureReReadXMLData("file://" + xmlPath);
        mDrawView.drawFromData();
        mDrawView.addCommand();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mDrawView.changePaintSize(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onColorChanged(int color) {
        mDrawView.changePaintColor(color);
    }

    @Override
    public void onSelectMode(int id) {
        if (id == R.id.rl_normal_mode_select) {
            mTVSelectMode.setText(R.string.mode_path);
            baseState = PathState.getInstance();
        } else if (id == R.id.rl_line_mode_select) {
            mTVSelectMode.setText(R.string.mode_line);
            baseState = LineState.getInstance();
        } else if (id == R.id.rl_rectangle_mode_select) {
            mTVSelectMode.setText(R.string.mode_rectangle);
            baseState = RectangleState.getInstance();
        } else if (id == R.id.rl_circle_mode_select) {
            mTVSelectMode.setText(R.string.mode_circle);
            baseState = CircleState.getInstance();
        } else {
            baseState = PathState.getInstance();
        }
        mDrawView.setCurrentState(baseState);
    }
    private void dataReset() {
        DrawDataUtils.getInstance().getSaveDrawDataList().clear();
        DrawDataUtils.getInstance().getShearDrawDataList().clear();
        CommandUtils.getInstance().getRedoCommandList().clear();
        CommandUtils.getInstance().getUndoCommandList().clear();
    }
    private void copy() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date currentDate = new Date(System.currentTimeMillis());
        String newFilePath = simpleDateFormat.format(currentDate);
        File bookFile = new File(NoteApplication.ROOT_DIRECTORY + "/" + newFilePath);
        if (!bookFile.exists()) {
            bookFile.mkdir();
        }
        FileUtils.fileCopy(NoteApplication.TEMPORARY_PATH, NoteApplication.ROOT_DIRECTORY + "/" + newFilePath);
        Intent intent = new Intent();
        String path = list.get(0).split("/")[list.get(0).split("/").length-1];
//        intent.putExtra("path", NoteApplication.ROOT_DIRECTORY + "/" + newFilePath+"/"+path);
//        intent.putExtra("filePath", NoteApplication.ROOT_DIRECTORY + "/" + newFilePath);
//        setResult(NoteApplication.OK, intent);
//        finish();
    }

    /**
     * 设置画笔的大小
     * @param widt
     */
    public void setDrawViewSize(float widt){
        mDrawView.changePaintSize(widt);
    }

    /**
     * 设置画笔颜色
     * @param color
     */
    public void setDrawViewColor(int color){
        mDrawView.changePaintColor(color);
    }
    /**
     * 调用默认的画笔设置颜色
     */
    public void showDrawViewColor(){
        mAppCompatDialog.show();
    }

    /**
     * 设置画笔模式
     * @param state  PathState默认  LineState直线   RectangleState矩形  CircleState圆形
     */
    public void setDrawViewModel(BaseState state){
        mDrawView.setCurrentState(state);
    }

    /**
     * 获取当前的画笔模式
     * @return
     */
    public BaseState getCurrsentState(){
        return baseState;
    }
    /**
     * 橡皮擦
     */
    public void setEraser(){
        mDrawView.setCurrentState(EraserState.getInstance());
    }
    /**
     * 展示推出前的确认信息
     */
    public void showMsg(){
        mBuilder.show();
    }

    /**
     * 撤销前一步
     */
    public void DrawBack(){
        mDrawView.setCanvasCode(NoteApplication.CANVAS_UNDO);
        mDrawView.invalidate();
    }

    /**
     * 前进
     */
    public void DrawGo(){
        mDrawView.setCanvasCode(NoteApplication.CANVAS_REDO);
        mDrawView.invalidate();
    }

    /**
     * 清除所有画笔信息
     */
    public void cleanAll(){
        mDrawView.setCanvasCode(NoteApplication.CANVAS_RESET);
        mDrawView.invalidate();
        dataReset();
    }

    /**
     * 保存当前画布
     */
    public void saveDraw(){
        if (mPageSize > 1) {
            copy();
        } else {
            save();
        }
    }

    /**
     * 清除所有画布
     */
    public void clearDraw(){
        NoteApplication.noteMap.clear();
    }
    private void getperr(){
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        permissionItems.add(new PermissionItem(Manifest.permission.READ_EXTERNAL_STORAGE));
        HiPermission.create(mContext)
                .permissions(permissionItems)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onFinish() {
                    //获取到的权限
                        if (mPageSize > 1) {
                            copy();
                        } else {
                            save();
                        }
                    }

                    @Override
                    public void onDeny(String permission, int position) {

                    }

                    @Override
                    public void onGuarantee(String permission, int position) {

                    }
                });
    }
}

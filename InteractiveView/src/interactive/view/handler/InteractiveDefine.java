package interactive.view.handler;

public class InteractiveDefine
{
	// �Ϥ��զ欰(�D��)
	public static final int	IMAGE_GESTURE_LONG_PRESS			= 100;	// ���
	public static final int	IMAGE_GESTURE_SPREAD				= 201;	// ����j
	public static final int	IMAGE_GESTURE_PINCH					= 202;	// ����Y�p

	// �q���欰(�Q��)
	public static final int	NOTIFICATION_RECV_FROM_DRAG			= 100;	// ������

	// �Ϥ�Ĳ�o�欰
	public static final int	IMAGE_EVENT_DRAG					= 100;	// ���
	public static final int	IMAGE_EVENT_SHOW_ITEM				= 200;	// ��ܪ���

	// ���s�ƥ�
	public static final int	BUTTON_TYPE_TAP						= 0;	// �I��
	public static final int	BUTTON_TYPE_LONG_PRESS				= 100;	// ���

	// ���sĲ�o�ƥ�
	public static final int	BUTTON_EVENT_DRAG					= 100;	// ���
	public static final int	BUTTON_EVENT_SHOW_ITEM				= 200;	// �u�X����
	public static final int	BUTTON_EVENT_VIDEO_PLAY				= 300;	// �v���
	public static final int	BUTTON_EVENT_VIDEO_PAUSE			= 301;	// �v��Ȱ�

	// �a������
	public static final int	MAP_TYPE_NORMAL						= 0;	// �@��
	public static final int	MAP_TYPE_SATELLITE					= 1;	// �ìP
	public static final int	MAP_TYPE_MIX						= 2;	// �V�X

	// slideshow����
	public static final int	SLIDESHOW_TYPE_NO_THUMBNAIL			= 0;	// �L�Y�ϦC
	public static final int	SLIDESHOW_TYPE_PAGE_CONTROL			= 1;	// ���I
	public static final int	SLIDESHOW_TYPE_THUMBNAIL			= 2;	// ���Y�ϦC

	// �������O
	public static final int	OBJECT_CATEGORY_IMAGE				= 10;	// �Ϥ�
	public static final int	OBJECT_CATEGORY_BUTTON				= 20;	// ���s
	public static final int	OBJECT_CATEGORY_VIDEO				= 30;	// �v��
	public static final int	OBJECT_CATEGORY_MAP					= 40;	// �a��
	public static final int	OBJECT_CATEGORY_POSTCARD			= 50;	// ��H�� 
	public static final int	OBJECT_CATEGORY_TICKET_BOOK			= 60;	// �C�O
	public static final int	OBJECT_CATEGORY_SLIDE_SHOW			= 70;	// slide show
	public static final int	OBJECT_CATEGORY_SHADOWING_WIDGET	= 80;	// ��Ū

	// �ʵe�ĪG�t�ΥN��(TBD)
	// �X�{
	public static final int	ANIMATION_TYPE_SHOW_NORMAL			= 0;	// �L�ʵe
	public static final int	ANIMATION_TYPE_SHOW_POPUP			= 1;	// �u�X
	public static final int	ANIMATION_TYPE_SHOW_FADE_ZOOM_IN	= 2;	// �H�J�i�}
	public static final int	ANIMATION_TYPE_SHOW_SHAKE			= 3;	// �n��

	// ��
	public static final int	ANIMATION_TYPE_HIDE_NORMAL			= 4;	// �L�ʵe
	public static final int	ANIMATION_TYPE_HIDE_FADE_ZOOM_OUT	= 5;	// �H�X�Y�p
	public static final int	ANIMATION_TYPE_HIDE_LEFT_UP			= 6;	// �Y�ܥ��W��
	public static final int	ANIMATION_TYPE_HIDE_LEFT_DOWN		= 7;	// �Y�ܥ��U��
	public static final int	ANIMATION_TYPE_HIDE_RIGHT_UP		= 8;	// �Y�ܥk�W��
	public static final int	ANIMATION_TYPE_HIDE_RIGHT_DOWN		= 9;	// �Y�ܥk�U��

	// �v��
	public static final int	VIDEO_TYPE_LOCAL					= 0;	// ���a�v��
	public static final int	VIDEO_TYPE_TOUTUBE					= 1;	// youtube
	public static final int	VIDEO_TYPE_URL						= 2;	// �����v��

	public InteractiveDefine()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void finalize() throws Throwable
	{
		// TODO Auto-generated method stub
		super.finalize();
	}

}

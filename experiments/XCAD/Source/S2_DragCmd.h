/////////////////////////////////////////////////////////////////////////
// All rights reserved
//
// Use and distribute freely, except: don't remove my name from the
// source or documentation (don't take credit for my work), mark your
// changes and extend the author list below, don't alter or remove this notice.
//
// Send bug reports, bug fixes, enhancements, requests, etc.,
// and I'll try to keep a version up to date.
// xiezheheng@yahoo.com
//
// Author list: Zheheng XIE
//
/////////////////////////////////////////////////////////////////////////


#pragma once
#include "fw_cmd.h"
#include "mh_point3.h"

class GR_DLNodeSketch2D;
class GR_SelectionSet;
class S2_DragCmd : public FW_Cmd
{
public:
	S2_DragCmd(void);
	virtual ~S2_DragCmd(void);

	static S2_DragCmd* GetCmd();

	// If the button in the command bar is clicked, then this function will be called
	virtual BOOL OnExecute();

	// Terminate the command. Such as when ESC is pressed
	virtual BOOL OnTerminate();

	// MouseMove message
	virtual void OnMouseMove(CWnd* pWnd, UINT nFlags, CPoint point);

	// LButtonDown message
	virtual void OnLButtonDown(CWnd* pWnd, UINT nFlags, CPoint point);

	// LButtonUp message
	virtual void OnLButtonUp(CWnd* pWnd, UINT nFlags, CPoint point);

private:
	DECLARE_MESSAGE_MAP()

	// The active sketch node
	GR_DLNodeSketch2D*	m_pDLNodeSketch2D;

	// The selection set that stores the selected sketch items
	GR_SelectionSet*	m_pSelSet;

	// Previous point in the window
	CPoint				m_ptPrev;

	// Previous point in the sketch
	MH_Point3			m_pt3Prev;
};

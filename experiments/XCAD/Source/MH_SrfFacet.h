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
#include "mh_obj.h"
#include "mh_srfpt.h"

class MH_SrfFacet :	public MH_Obj
{
public:
	MH_SrfFacet(const MH_SrfPt& srfPt1, const MH_SrfPt& srfPt2, const MH_SrfPt& srfPt3);
	virtual ~MH_SrfFacet(void);

	// Transform this object
	virtual void Transform(const MH_Matrix44& mtx);

//private:
	MH_SrfPt m_srfPt[3];
};

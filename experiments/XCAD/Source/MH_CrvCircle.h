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
#include "mh_crv.h"
#include "mh_point3.h"
#include "mh_vector3.h"
#include "mh_crvnurbs.h"

class MH_CrvCircle : public MH_Crv
{
public:
	MH_CrvCircle(void);
	MH_CrvCircle(const MH_Point3& ptCenter, const MH_Vector3& vNormal, float fRadius);
	virtual ~MH_CrvCircle(void);

    // Tessellate the curve
	// The points are stored in m_vPts
	virtual bool		Tessellate(float fTolerance);

	// Transform this object
	virtual void		Transform(const MH_Matrix44& mtx);

	// Copy this curve
	virtual MH_Crv*		Copy();

	// Get the nurbs expression
	virtual MH_CrvNurbs GetNurbs() const;

	// Assignment
	MH_CrvCircle&		operator = (const MH_CrvCircle& crv);

	// Get center of the circle
    const MH_Point3&	GetCenter() const;

	// Get normal of the circle
	const MH_Vector3&	GetNormal() const;

	// Get radius of the circle
	float				GetRadius() const;
private:
	MH_Point3	m_ptCenter;
	MH_Vector3	m_vNormal;
	float		m_fRadius;
};

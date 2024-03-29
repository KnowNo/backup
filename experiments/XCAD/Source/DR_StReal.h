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
#include "dr_st.h"

class DR_StReal : public DR_St
{
public:
	DR_StReal(const SL_Ptr<DR_Fragment>& pFragment);
	virtual ~DR_StReal(void);

	// Get value of the expression
	float GetValue() const;

private:
	CString m_strExpression;
};

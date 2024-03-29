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
#include "resource.h"
#include "xcadshell.h"

class FW_UICmdBars;
class ATL_NO_VTABLE CFWUICmdBars : 
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CFWUICmdBars, &CLSID_FWUICmdBars>,
	public IDispatchImpl<IFWUICmdBars, &IID_IFWUICmdBars, &LIBID_XCADShell, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CFWUICmdBars();

DECLARE_REGISTRY_RESOURCEID(IDR_FWUICMDBARS)
DECLARE_PROTECT_FINAL_CONSTRUCT()

BEGIN_COM_MAP(CFWUICmdBars)
	COM_INTERFACE_ENTRY(IFWUICmdBars)
	COM_INTERFACE_ENTRY(IDispatch)
END_COM_MAP()

	HRESULT FinalConstruct();	
	void	FinalRelease();

public:
	STDMETHOD(Add)(BSTR bstrInternalName, BSTR bstrDisplayName, IFWUICmdBar* *ppVal);
	STDMETHOD(Item)(/*[in]*/ VARIANT Index, /*[out, retval]*/ IFWUICmdBar** ppVal);
	STDMETHOD(get__NewEnum)(/*[out, retval]*/ IUnknown* *ppVal);
	STDMETHOD(get_Count)(/*[out, retval]*/ long *pVal);
	STDMETHOD(get_Parent)(/*[out, retval]*/ IFWUIManager* *ppVal);
private:
	FW_UICmdBars* m_pUICmdBars;
};

OBJECT_ENTRY_AUTO(__uuidof(FWUICmdBars), CFWUICmdBars)

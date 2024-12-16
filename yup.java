public class DealSyncValidator {

    @PostConstruct
    public void test() {
        checkSync(14644);
    }

    public SyncReport checkSync(long dealId) {
        return dealRepo.findById(dealId)
                .map(eDeal -> {
                    EDealHandler eDealHandler = fetchDealHandler(dealId);

                    DealDto dealFromCS = fetchDealFromCS(eDealHandler);
                    DealDto mappedDeal = fetchMappedDealFromLDT(eDeal);

                    Set<String> ignoredFields = getIgnoredFields();

                    SyncReport report = new SyncReport(dealId);
                    report.addMismatches(CompareTool.compare(mappedDeal, dealFromCS, ignoredFields));

                    if (!report.getMismatches().isEmpty()) {
                        report.addWaitingEvents(fetchWaitingEvents(dealId));
                    }

                    report.calculateStatus();
                    report.printReport();

                    return report;
                })
                .orElseThrow(() -> new RuntimeException("Deal: " + dealId + " does not exist in LDT"));
    }

    private EDealHandler fetchDealHandler(long dealId) {
        return dealHandlerRepo.findByObjectIdAndObjectType(dealId, ObjectType.DEAL)
                .orElseThrow(() -> new RuntimeException("Deal: " + dealId + " does not exist in dealHandler table"));
    }

    private DealDto fetchDealFromCS(EDealHandler eDealHandler) {
        return dealStructureFetcher.fetchByFnId(eDealHandler.getFunctionalId(), eDealHandler.getVersionId());
    }

    private DealDto fetchMappedDealFromLDT(EDeal eDeal) {
        DealDto mappedDeal = dealMapper.toCSDealDto(eDeal);
        mappedDeal.setFunctionalId(eDealHandler.getFunctionalId());
        mappedDeal.setFunctionalVersion(eDealHandler.getVersionId());

        List<DealTrancheDto> mappedTranches = trancheRepo.findAllByDealId(eDeal.getId()).stream()
                .map(eTranche -> {
                    DealTrancheDto mappedTranche = trancheMapper.toTrancheDto(eTranche);
                    mappedTranche.setFunctionalId(eTranche.getFunctionalId());
                    return mappedTranche;
                })
                .toList();

        mappedDeal.setCurrentTranches(mappedTranches);
        return mappedDeal;
    }

    private Set<String> getIgnoredFields() {
        return Set.of(
                "dealLenders[*].creditCommitteeByLenders[*].creditCommitteeType",
                "originatorGlossary.bomBom[*].functionalId",
                "currentTranches[*].inputStatus"
        );
    }

    private List<String> fetchWaitingEvents(long dealId) {
        return waitingEventTransactionService.fetchWaitingEvents(dealId);
    }
}
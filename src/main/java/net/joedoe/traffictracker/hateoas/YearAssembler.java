package net.joedoe.traffictracker.hateoas;

import jakarta.annotation.Nonnull;
import net.joedoe.traffictracker.controller.MonthController;
import net.joedoe.traffictracker.controller.YearController;
import net.joedoe.traffictracker.dto.YearDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class YearAssembler implements RepresentationModelAssembler<YearDto, EntityModel<YearDto>> {
    @Nonnull
    @Override
    public EntityModel<YearDto> toModel(@Nonnull YearDto year) {
        EntityModel<YearDto> model = EntityModel.of(year);
        model.add(linkTo(methodOn(YearController.class).getYearByDate(year.getYear())).withSelfRel());
        if (year.isPrev())
            model.add(linkTo(methodOn(YearController.class).getYearByDate(year.getYear() - 1)).withRel("prev_year"));
        if (year.isNext())
            model.add(linkTo(methodOn(YearController.class).getYearByDate(year.getYear() + 1)).withRel("next_year"));
        model.add(linkTo(methodOn(MonthController.class).getMonthByDate(year.getYear(), 1)).withRel("months"));
        return model;
    }
}
